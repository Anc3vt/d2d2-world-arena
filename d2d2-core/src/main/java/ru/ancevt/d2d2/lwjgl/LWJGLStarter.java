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
package ru.ancevt.d2d2.lwjgl;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.D2D2Starter;
import ru.ancevt.d2d2.display.IRenderer;
import ru.ancevt.d2d2.display.ScaleMode;
import ru.ancevt.d2d2.display.Stage;
import ru.ancevt.d2d2.display.text.BitmapFont;
import ru.ancevt.d2d2.event.EventPool;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.input.Mouse;
import ru.ancevt.d2d2.touch.TouchProcessor;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LWJGLStarter implements D2D2Starter {

    private static final String DEMO_TEXTURE_DATA_INF_FILE = "d2d2-core-demo-texture-data.inf";

    private static final String DEFAULT_BITMAP_FONT = "Terminus.bmf";

    private IRenderer renderer;
    private long windowId;
    private boolean mouseVisible;

    private int width;
    private int height;
    private String title;

    private boolean visible;

    private int mouseX;
    private int mouseY;
    private boolean isDown;

    private Stage stage;

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
        renderer = new Renderer(stage);
        ((Renderer)renderer).setLWJGLTextureEngine((LWJGLTextureEngine) D2D2.getTextureManager().getTextureEngine());
        windowId = createWindow();
        setVisible(true);
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

        //glfwWindowHint(GLFW.GLFW_SAMPLES, 64);

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
                stage.getRoot().dispatchEvent(EventPool.createInputEvent(
                        InputEvent.MOUSE_WHEEL,
                        stage.getRoot(),
                        Mouse.getX(),
                        Mouse.getY(),
                        0,
                        (int) dy,
                        isDown,
                        0,
                        0,
                        (char) 0,
                        false,
                        false,
                        false
                ));
            }
        });

        glfwSetMouseButtonCallback(resultWindowId, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                isDown = action == 1;

                stage.getRoot().dispatchEvent(EventPool.createInputEvent(
                        action == 1 ? InputEvent.MOUSE_DOWN : InputEvent.MOUSE_UP,
                        stage.getRoot(),
                        Mouse.getX(),
                        Mouse.getY(),
                        button,
                        0,
                        isDown,
                        0,
                        0,
                        (char) 0,
                        false,
                        false,
                        false
                ));

                TouchProcessor.instance.screenTouch(mouseX, mouseY, 0, isDown);
            }
        });

        glfwSetCursorPosCallback(resultWindowId, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {

                mouseX = (int) x;
                mouseY = (int) y;

                Mouse.setXY(getTransformedX(mouseX), getTransformedY(mouseY));

                stage.getRoot().dispatchEvent(EventPool.createInputEvent(
                        InputEvent.MOUSE_MOVE,
                        stage.getRoot(),
                        Mouse.getX(),
                        Mouse.getY(),
                        0,
                        0,
                        isDown,
                        0,
                        0,
                        (char) 0,
                        false,
                        false,
                        false
                ));

                TouchProcessor.instance.screenDrag(0, mouseX, mouseY);
            }
        });

        glfwSetCharCallback(resultWindowId, (window, codepoint) -> {
            stage.getRoot().dispatchEvent(EventPool.createInputEvent(
                    InputEvent.KEY_TYPE,
                    stage.getRoot(),
                    0,
                    0,
                    0,
                    0,
                    false,
                    0,
                    0,
                    (char) 0,
                    false,
                    false,
                    false,
                    codepoint,
                    String.valueOf(Character.toChars(codepoint)))
            );
        });

        glfwSetKeyCallback(resultWindowId, (window, key, scancode, action, mods) -> {

            if (action == GLFW_PRESS) {
                stage.getRoot().dispatchEvent(EventPool.createInputEvent(
                        InputEvent.KEY_DOWN,
                        stage.getRoot(),
                        0,
                        0,
                        0,
                        0,
                        false,
                        0,
                        key,
                        (char) key,
                        (mods & GLFW_MOD_SHIFT) != 0,
                        (mods & GLFW_MOD_CONTROL) != 0,
                        (mods & GLFW_MOD_ALT) != 0
                ));
            } else if (action == GLFW_RELEASE) {
                stage.getRoot().dispatchEvent(EventPool.createInputEvent(
                        InputEvent.KEY_UP,
                        stage.getRoot(),
                        0,
                        0,
                        0,
                        0,
                        false,
                        0,
                        key,
                        (char) key,
                        (mods & GLFW_MOD_SHIFT) != 0,
                        (mods & GLFW_MOD_CONTROL) != 0,
                        (mods & GLFW_MOD_ALT) != 0
                ));
            }
        });

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert videoMode != null;
        glfwSetWindowPos(
                resultWindowId,
                (videoMode.width() - width) / 2,
                20 //(videoMode.height() - height) / 2
        );

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
