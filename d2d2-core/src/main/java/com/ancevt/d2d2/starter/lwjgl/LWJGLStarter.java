/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2.starter.lwjgl;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.D2D2Starter;
import com.ancevt.d2d2.display.IRenderer;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.touch.TouchProcessor;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LWJGLStarter implements D2D2Starter {

    private static final String DEMO_TEXTURE_DATA_INF_FILE = "d2d2-core-demo-texture-data.inf";

    private static final String DEFAULT_BITMAP_FONT = "Terminus.bmf";

    private IRenderer renderer;
    long windowId;
    private boolean mouseVisible;

    private int width;
    private int height;
    private String title;

    private boolean visible;

    private int mouseX;
    private int mouseY;
    private boolean isDown;

    private Stage stage;
    private boolean fullscreen;
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private int videoModeWidth;
    private int videoModeHeight;

    public LWJGLStarter(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        D2D2.getTextureManager().setTextureEngine(new LWJGLTextureEngine());
    }

    @Override
    public void stop() {
        glfwSetWindowShouldClose(windowId, true);
    }

    @Override
    public void setMouseVisible(boolean mouseVisible) {
        this.mouseVisible = mouseVisible;
        glfwSetInputMode(windowId, GLFW_CURSOR, mouseVisible ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_HIDDEN);
    }

    @Override
    public boolean isMouseVisible() {
        return mouseVisible;
    }

    @Override
    public void create() {
        stage = new Stage();
        stage.setStageSize(width, height);
        stage.onResize(width, height);
        renderer = new LWJGLRenderer(stage, this);
        ((LWJGLRenderer) renderer).setLWJGLTextureEngine((LWJGLTextureEngine) D2D2.getTextureManager().getTextureEngine());
        windowId = createWindow();
        setVisible(true);
    }

    @Override
    public void setSmoothMode(boolean value) {
        ((LWJGLRenderer) renderer).smoothMode = value;
    }

    @Override
    public boolean isSmoothMode() {
        return ((LWJGLRenderer) renderer).smoothMode;
    }

    @Override
    public void start() {
        startRenderLoop();
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;

        glfwSetWindowSize(windowId, width, height);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void setVisible(boolean value) {
        this.visible = value;
        if (value) {
            glfwShowWindow(windowId);
        } else {
            glfwHideWindow(windowId);
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    private long createWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();

        glfwWindowHint(GLFW.GLFW_SAMPLES, 64);

        long resultWindowId = glfwCreateWindow(width, height, title, NULL, NULL);

        if (resultWindowId == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetWindowSizeCallback(resultWindowId, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long l, int width, int height) {
                renderer.reshape(width, height);
                stage.onResize(width, height);
            }
        });

        glfwSetScrollCallback(resultWindowId, new GLFWScrollCallback() {
            @Override
            public void invoke(long win, double dx, double dy) {
                stage.getRoot().dispatchEvent(InputEvent.builder()
                        .type(InputEvent.MOUSE_WHEEL)
                        .x(Mouse.getX())
                        .y(Mouse.getY())
                        .delta((int) dy)
                        .drag(isDown)
                        .build());
            }
        });

        glfwSetMouseButtonCallback(resultWindowId, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                isDown = action == 1;

                stage.getRoot().dispatchEvent(InputEvent.builder()
                        .type(action == 1 ? InputEvent.MOUSE_DOWN : InputEvent.MOUSE_UP)
                        .x(Mouse.getX())
                        .y(Mouse.getY())
                        .drag(isDown)
                        .mouseButton(button)
                        .build());

                TouchProcessor.instance.screenTouch(mouseX, mouseY, 0, isDown);
            }
        });

        glfwSetCursorPosCallback(resultWindowId, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                mouseX = (int) x;
                mouseY = (int) y;

                //Mouse.setXY(getTransformedX(mouseX), getTransformedY(mouseY));
                Mouse.setXY(mouseX, mouseY);

                stage.getRoot().dispatchEvent(InputEvent.builder()
                        .type(InputEvent.MOUSE_MOVE)
                        .x(Mouse.getX())
                        .y(Mouse.getY())
                        .drag(isDown)
                        .build());

                TouchProcessor.instance.screenDrag(0, mouseX, mouseY);
            }
        });

        glfwSetCharCallback(resultWindowId, (window, codepoint) -> {
            stage.getRoot().dispatchEvent(InputEvent.builder()
                    .type(InputEvent.KEY_TYPE)
                    .x(Mouse.getX())
                    .y(Mouse.getY())
                    .drag(isDown)
                    .codepoint(codepoint)
                    .keyType(String.valueOf(Character.toChars(codepoint)))
                    .build());
        });

        glfwSetKeyCallback(resultWindowId, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                stage.getRoot().dispatchEvent(InputEvent.builder()
                        .type(InputEvent.KEY_DOWN)
                        .x(Mouse.getX())
                        .y(Mouse.getY())
                        .keyChar((char) key)
                        .keyCode(key)
                        .drag(isDown)
                        .shift((mods & GLFW_MOD_SHIFT) != 0)
                        .control((mods & GLFW_MOD_CONTROL) != 0)
                        .alt((mods & GLFW_MOD_ALT) != 0)
                        .build());
            } else if (action == GLFW_RELEASE) {
                stage.getRoot().dispatchEvent(InputEvent.builder()
                        .type(InputEvent.KEY_UP)
                        .x(Mouse.getX())
                        .y(Mouse.getY())
                        .keyCode(key)
                        .keyChar((char) key)
                        .drag(isDown)
                        .shift((mods & GLFW_MOD_SHIFT) != 0)
                        .control((mods & GLFW_MOD_CONTROL) != 0)
                        .alt((mods & GLFW_MOD_ALT) != 0)
                        .build());
            }
        });

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert videoMode != null;

        if (System.getProperty("window100400") != null) {
            glfwSetWindowPos(
                    resultWindowId,
                    100,
                    400 //(videoMode.height() - height) / 2
            );
        } else {
            glfwSetWindowPos(
                    resultWindowId,
                    (videoMode.width() - width) / 2,
                    (videoMode.height() - height) / 2
            );
        }

        videoModeWidth = videoMode.width();
        videoModeHeight = videoMode.height();

        glfwMakeContextCurrent(resultWindowId);
        GL.createCapabilities();

        glfwSwapInterval(1);

        BitmapFont.setDefaultBitmapFont(BitmapFont.loadBitmapFont(DEFAULT_BITMAP_FONT));

        // TODO: remove loading demo texture data info from here
        D2D2.getTextureManager().loadTextureDataInfo(DEMO_TEXTURE_DATA_INF_FILE);

        renderer.init(resultWindowId);
        renderer.reshape(width, height);

        return resultWindowId;
    }

    @Override
    public void putToClipboard(String string) {
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(
                        new StringSelection(string),
                        null
                );
    }

    @Override
    public String getStringFromClipboard() {
        try {
            return Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor).toString();
        } catch (UnsupportedFlavorException e) {
            //e.printStackTrace(); // ignore exception
            return "";
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void setFullscreen(boolean value) {

        if (!fullscreen) {
            int x[] = new int[1];
            int y[] = new int[1];
            int w[] = new int[1];
            int h[] = new int[1];
            glfwGetWindowPos(windowId, x, y);
            glfwGetWindowSize(windowId, w, h);
            windowX = x[0];
            windowY = y[0];
            windowWidth = w[0];
            windowHeight = h[0];
        }

        if (value) {
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
            glfwSetWindowPos(windowId, 0, 0);
            glfwSetWindowSize(windowId, videoModeWidth, videoModeHeight);
        } else {
            glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
            glfwSetWindowPos(windowId, windowX, windowY);
            glfwSetWindowSize(windowId, windowWidth, windowHeight);
        }

        this.fullscreen = value;
    }

    @Override
    public boolean isFullscreen() {
        return fullscreen;
    }

    private int getTransformedX(int x) {
        if (stage.getScaleMode() == ScaleMode.REAL) return x;

        float dWidth = stage.getWidth();
        float rWidth = stage.getStageWidth();
        return (int) (x * (rWidth / dWidth));
    }

    private int getTransformedY(int y) {
        if (stage.getScaleMode() == ScaleMode.REAL) return y;

        float dHeight = stage.getHeight();
        float rHeight = stage.getStageHeight();
        return (int) (y * (rHeight / dHeight));
    }

    private void startRenderLoop() {
        while (!glfwWindowShouldClose(windowId)) {
            glfwPollEvents();
            renderer.renderFrame();
            glfwSwapBuffers(windowId);
        }

        glfwTerminate();
    }
}
