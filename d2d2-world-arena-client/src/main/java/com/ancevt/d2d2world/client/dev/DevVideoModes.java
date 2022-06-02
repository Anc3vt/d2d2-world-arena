/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancevt.d2d2world.client.dev;

import com.ancevt.commons.Holder;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.GLFWUtils;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.IContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.client.settings.MonitorManager;
import com.ancevt.d2d2world.client.ui.chat.Chat;
import com.ancevt.d2d2world.client.ui.chat.ChatEvent;
import com.ancevt.util.args.Args;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.ancevt.d2d2.D2D2.stage;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;

public class DevVideoModes {

    private static final Set<Command> commands = new HashSet<>();

    public static void main(String[] args) {
        //Root root = D2D2.init(new LWJGLStarter(1920, 1080, "(floating)"));
        Stage stage = D2D2.init(new LWJGLBackend(640, 480, "(floating)"));

        TextureAtlas textureAtlas = D2D2.getTextureManager().loadTextureAtlas("d2d2-picture-test.png");
        Sprite sprite = new Sprite(textureAtlas.createTexture());

        IContainer container = new Container();
        container.add(sprite);

        //D2D2.setFullscreen(true);

        long windowId = D2D2.getBackend().getWindowId();

        MonitorManager.getInstance().setMonitorDeviceId(glfwGetPrimaryMonitor());

        VideoMode previousVideoMode = GLFWUtils.getVideoMode(MonitorManager.getInstance().getMonitorDeviceId());

        GLFWUtils.setVideoMode(
                MonitorManager.getInstance().getMonitorDeviceId(),
                windowId,
                previousVideoMode
        );

        Chat chat = Chat.getInstance();

        stage().addEventListener(Event.RESIZE, event -> {
            chat.addMessage("Resize " + stage().getWidth() + "x" + stage().getHeight());

            while(sprite.getHeight() * sprite.getScaleY() > stage().getHeight()) {
                sprite.setScaleY(sprite.getScaleY() - 0.01f);
                sprite.setScaleX(sprite.getScaleY());
            }

            while(sprite.getHeight() * sprite.getScaleY() < stage().getHeight()) {
                sprite.setScaleY(sprite.getScaleY() + 0.01f);
                sprite.setScaleX(sprite.getScaleY());
            }
        });

        stage.addEventListener(InputEvent.KEY_DOWN, event -> {
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
            chat.addMessage(GLFWUtils.getMonitors().toString());
            return true;
        }));

        commands.add(new Command("/list", a -> {
            GLFWVidMode.Buffer glfwVidModes = GLFW.glfwGetVideoModes(MonitorManager.getInstance().getMonitorDeviceId());
            List<GLFWVidMode> list = glfwVidModes.stream().toList();
            list.forEach(m -> chat.addMessage(m.width() + "x" + m.height() + " " + m.refreshRate()));
            return true;
        }));

        commands.add(new Command("/vm", a -> {
            int width = a.get(int.class, 1, 0);
            int height = a.get(int.class, 2, 0);
            int refreshRate = a.get(int.class, 3, 0);

            Holder<Boolean> found = new Holder<>(false);

            GLFW.glfwGetVideoModes(MonitorManager.getInstance().getMonitorDeviceId()).stream().toList().forEach(glfwVidMode -> {
                if (glfwVidMode.width() == width &&
                        glfwVidMode.height() == height &&
                        (glfwVidMode.refreshRate() == refreshRate || refreshRate == -1)) {

                    found.setValue(true);

                    chat.addMessage(width + "x" + height + " " + refreshRate);
                    chat.setHeight(height - 30);

                    GLFWUtils.setVideoMode(
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

        stage.add(container);
        stage.add(chat, 10, 10);

        D2D2.loop();
        chat.saveHistory();
        GLFWUtils.linuxCare(MonitorManager.getInstance().getMonitorDeviceId(), previousVideoMode);
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
