package com.ancevt.d2d2world.client.dev;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.GLFWUtils;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.client.settings.MonitorManager;
import com.ancevt.d2d2world.client.ui.UiTextInputProcessor;
import com.ancevt.d2d2world.client.ui.chat.Chat;
import com.ancevt.d2d2world.client.ui.chat.ChatEvent;
import com.ancevt.util.command.CommandSet;
import com.ancevt.util.command.NoSuchCommandException;

import static com.ancevt.d2d2.D2D2.getStage;

public class DevVideoModes2 {

    private static CommandSet<Void> commandSet = new CommandSet<>();
    private static Chat chat;
    private static Sprite sprite;

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        TextureAtlas textureAtlas = D2D2.getTextureManager().loadTextureAtlas("d2d2-picture-test.png");
        sprite = new Sprite(textureAtlas.createTexture());
        sprite.setAlpha(0.1f);
        root.add(sprite);

        chat = new Chat();
        root.add(chat, 10f, 10f);

        MonitorManager.getInstance().rememberResolutionAsStart();

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
            chat.print(e.getText(), Color.GREEN);
            try {
                commandSet.execute(e.getText());
            } catch (NoSuchCommandException ex) {
                chat.print(ex.getMessage(), Color.RED);
            }
            chat.openInput();
        });

        registerCommands();

        getStage().addEventListener(Event.RESIZE, event -> {
            chat.print("Resize " + getStage().getWidth() + "x" + getStage().getHeight());
            fix();
        });

        fix();

        UiTextInputProcessor.enableRoot(root);

        chat.openInput();

        D2D2.loop();
        exit();
    }

    private static void registerCommands() {
        commandSet.registerCommand("/help", "Print registered command list", args -> {
            commandSet.getFormattedCommandList().lines().forEach(line -> chat.print(line));
            return null;
        });

        commandSet.registerCommand("/test", "Run and log video mode test", args -> {
            chat.print("Start video mode test");
            startTest();
            return null;
        });

        commandSet.registerCommand("/monitors", "Print monitor list", args -> {
            GLFWUtils.getMonitors().values().forEach(monitorName -> chat.print(monitorName));
            return null;
        });

        commandSet.registerCommand("/info", "Print current MonitorDevice state", args -> {
            MonitorManager monitorManager = MonitorManager.getInstance();
            chat.print("Monitor device id: " + monitorManager.getMonitorDeviceId());
            chat.print("Monitor device name: " + monitorManager.getMonitorDeviceName());
            chat.print("Fullscreen: " + monitorManager.isFullscreen());
            chat.print("Resolution: " + monitorManager.getResolution());
            chat.print("Start resolution: " + monitorManager.getStartResolution());
            return null;
        });

        commandSet.registerCommand("/vm", "Print current video mode", args -> {
            VideoMode videoMode = GLFWUtils.getVideoMode(MonitorManager.getInstance().getMonitorDeviceId());
            chat.print(videoMode.getResolution() + " " + videoMode.getRefreshRate());
            return null;
        });

        commandSet.registerCommand("/vms", "Print video mode list", args -> {
            GLFWUtils.getVideoModes(MonitorManager.getInstance().getMonitorDeviceId()).forEach(videoMode -> {
                chat.print(videoMode.getResolution() + " " + videoMode.getRefreshRate());
            });
            return null;
        });

        commandSet.registerCommand("/maxvm", "Print max video mode", args -> {
            VideoMode maxVideoMode = GLFWUtils.getMaxVideoMode(MonitorManager.getInstance().getMonitorDeviceId());
            chat.print(maxVideoMode.getResolution() + " " + maxVideoMode.getRefreshRate());
            return null;
        });

        commandSet.registerCommand("/setmaxvm", "Set max video mode", args -> {
            VideoMode maxVideoMode = GLFWUtils.getMaxVideoMode(MonitorManager.getInstance().getMonitorDeviceId());
            MonitorManager.getInstance().setResolution(maxVideoMode.getResolution());
            return null;
        });

        commandSet.registerCommand("/fs", "Print if fullscreen mode is on", args->{
            chat.print("" + MonitorManager.getInstance().isFullscreen());
            return null;
        });

        commandSet.registerCommand("/setfs", "Set full screen", args -> {
            MonitorManager.getInstance().setFullscreen(args.get(boolean.class, 1, false));
            return null;
        });

        commandSet.registerCommand("/nextmonitor", "Set the next monitor as current", args -> {

            return null;
        });

        commandSet.registerCommand("/donothing", "Do nothing", args -> null);

        commandSet.registerCommand("/q", "Exit", args -> {
            exit();
            return null;
        });
    }

    private static void startTest() {
        String[] script = new String[]{
                "/help",
                "/monitors",
                "/info",
                "/vm",
                "/maxvm",
                "/vms",
                "/setmaxvm",
                "/setfs true",
                "/fs",
                "/setfs false",
                "/fs",
        };

        Async.run(() -> {
            for (String command : script) {
                chat.print(command, Color.YELLOW);
                try {
                    commandSet.execute(command);
                } catch (NoSuchCommandException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            chat.print("Test complete", Color.GRAY);
        });
    }

    private static void exit() {
        chat.saveHistory();
        System.exit(0);
    }

    private static void fix() {
        while (sprite.getWidth() * sprite.getScaleX() > getStage().getWidth()) {
            sprite.setScaleX(sprite.getScaleX() - 0.01f);
            sprite.setScaleY(sprite.getScaleX());
        }

        while (sprite.getWidth() * sprite.getScaleX() < getStage().getWidth()) {
            sprite.setScaleX(sprite.getScaleX() + 0.01f);
            sprite.setScaleY(sprite.getScaleX());
        }

        chat.setHeight(getStage().getHeight() - 30);
    }

}
