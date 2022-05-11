/*
 *   D2D2 World Arena Client
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2world.client.dev;

import com.ancevt.commons.Holder;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.backend.lwjgl.LWJGLVideoModeUtils;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.client.settings.MonitorDevice;
import com.ancevt.d2d2world.client.ui.UiTextInputProcessor;
import com.ancevt.d2d2world.client.ui.chat.Chat;
import com.ancevt.d2d2world.client.ui.chat.ChatEvent;
import com.ancevt.util.args.Args;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.ancevt.d2d2.D2D2.getStage;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;

public class DevVideoModes {

    private static final Set<Command> commands = new HashSet<>();

    public static void main(String[] args) {
        //Root root = D2D2.init(new LWJGLStarter(1920, 1080, "(floating)"));
        Root root = D2D2.init(new LWJGLBackend(640, 480, "(floating)"));

        TextureAtlas textureAtlas = D2D2.getTextureManager().loadTextureAtlas("d2d2-picture-test.png");
        Sprite sprite = new Sprite(textureAtlas.createTexture());

        IDisplayObjectContainer container = new DisplayObjectContainer();
        container.add(sprite);

        UiTextInputProcessor.enableRoot(root);

        //D2D2.setFullscreen(true);

        long windowId = D2D2.getBackend().getWindowId();

        MonitorDevice.getInstance().setMonitorDeviceId(glfwGetPrimaryMonitor());

        VideoMode previousVideoMode = LWJGLVideoModeUtils.getVideoMode(MonitorDevice.getInstance().getMonitorDeviceId());

        LWJGLVideoModeUtils.setVideoMode(
                MonitorDevice.getInstance().getMonitorDeviceId(),
                windowId,
                previousVideoMode
        );

        Chat chat = Chat.getInstance();

        getStage().addEventListener(Event.RESIZE, event -> {
            chat.addMessage("Resize " + getStage().getWidth() + "x" + getStage().getHeight());

            while(sprite.getHeight() * sprite.getScaleY() > getStage().getHeight()) {
                sprite.setScaleY(sprite.getScaleY() - 0.01f);
                sprite.setScaleX(sprite.getScaleY());
            }

            while(sprite.getHeight() * sprite.getScaleY() < getStage().getHeight()) {
                sprite.setScaleY(sprite.getScaleY() + 0.01f);
                sprite.setScaleX(sprite.getScaleY());
            }
        });

        root.addEventListener(InputEvent.KEY_DOWN, event -> {
            InputEvent inputEvent = (InputEvent) event;
            switch (inputEvent.getKeyCode()) {
                case KeyCode.PAGE_UP -> {
                    chat.setScroll(chat.getScroll() - 10);
                }

                case KeyCode.PAGE_DOWN -> {
                    chat.setScroll(chat.getScroll() + 10);
                }

                case KeyCode.F8 -> {
                    chat.setShadowEnabled(!chat.isShadowEnabled());
                }

                case KeyCode.F6, KeyCode.T -> {
                    if (!chat.isInputOpened()) chat.openInput();
                }
            }
        });
        chat.addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            var e = (ChatEvent) event;
            if (!processCommand(e.getText())) {
                chat.addMessage("Unknown command: " + e.getText(), Color.RED);
            }
        });
        chat.openInput();

        commands.add(new Command("/monitors", a-> {
            chat.addMessage(LWJGLVideoModeUtils.getMonitors().toString());
            return true;
        }));

        commands.add(new Command("/list", a -> {
            GLFWVidMode.Buffer glfwVidModes = GLFW.glfwGetVideoModes(MonitorDevice.getInstance().getMonitorDeviceId());
            List<GLFWVidMode> list = glfwVidModes.stream().toList();
            list.forEach(m -> chat.addMessage(m.width() + "x" + m.height() + " " + m.refreshRate()));
            return true;
        }));

        commands.add(new Command("/vm", a -> {
            int width = a.get(int.class, 1, 0);
            int height = a.get(int.class, 2, 0);
            int refreshRate = a.get(int.class, 3, 0);

            Holder<Boolean> found = new Holder<>(false);

            GLFW.glfwGetVideoModes(MonitorDevice.getInstance().getMonitorDeviceId()).stream().toList().forEach(glfwVidMode -> {
                if (glfwVidMode.width() == width &&
                        glfwVidMode.height() == height &&
                        (glfwVidMode.refreshRate() == refreshRate || refreshRate == -1)) {

                    found.setValue(true);

                    chat.addMessage(width + "x" + height + " " + refreshRate);
                    chat.setHeight(height - 30);

                    LWJGLVideoModeUtils.setVideoMode(
                            glfwGetPrimaryMonitor(),
                            windowId,
                            width,
                            height,
                            refreshRate
                    );
                }
            });

            if (!found.getValue()) {
                chat.addMessage("vid mode not found");
            }

            return true;
        }));

        commands.add(new Command("/q", a -> {
            D2D2.exit();
            return true;
        }));

        root.add(container);
        root.add(chat, 10, 10);

        D2D2.loop();
        chat.saveHistory();
        LWJGLVideoModeUtils.linuxCare(MonitorDevice.getInstance().getMonitorDeviceId(), previousVideoMode);
    }

    private static boolean processCommand(String text) {
        Args tokens = Args.of(text);
        String command = tokens.get(String.class, 0);
        Holder<Boolean> result = new Holder<>(false);
        commands.stream()
                .filter(c -> c.command.equals(command))
                .findAny()
                .ifPresent(c -> result.setValue(c.function().apply(tokens)));
        return result.getValue();
    }

    public record Command(String command, Function<Args, Boolean> function) {
    }

}
