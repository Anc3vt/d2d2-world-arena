
package com.ancevt.d2d2world.client;

import com.ancevt.commons.properties.PropertyWrapper;
import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.backend.lwjgl.GLFWUtils;
import com.ancevt.d2d2.debug.DebugPanel;
import com.ancevt.d2d2.media.SoundSystem;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.client.scene.GameRoot;
import com.ancevt.d2d2world.client.scene.intro.IntroRoot;
import com.ancevt.d2d2world.client.settings.ClientConfig;
import com.ancevt.d2d2world.client.settings.MonitorManager;
import com.ancevt.d2d2world.client.ui.chat.Chat;
import com.ancevt.util.args.Args;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Properties;

import static com.ancevt.d2d2.D2D2.getStage;
import static com.ancevt.d2d2.backend.lwjgl.OSDetector.isUnix;
import static com.ancevt.d2d2world.client.settings.ClientConfig.CONFIG;
import static com.ancevt.d2d2world.client.settings.ClientConfig.SOUND_ENABLED;

@Slf4j
public class D2D2WorldArenaClientMain {

    private static VideoMode startVideoMode;

    @SneakyThrows
    public static void main(String @NotNull [] args) throws IOException {
        CONFIG.load();
        for (String arg : args) {
            if (arg.startsWith("-P")) {
                arg = arg.substring(2);
                String[] split = arg.split("=");
                String key = split[0];
                String value = split[1];
                CONFIG.setProperty(key, value);
            } else if (arg.startsWith("-S")) {
                arg = arg.substring(2);
                String[] split = arg.split("=");
                String key = split[0];
                String value = split[1];
                System.setProperty(key, value);
            } else if (arg.equals("--debug")) {
                DebugPanel.setEnabled(true);
            }
            if (arg.equals("--colorize-logs")) {
                UnixDisplay.setEnabled(true);
            }
        }

        PropertyWrapper.argsToProperties(args, System.getProperties());

        SoundSystem.setEnabled(CONFIG.getBoolean(SOUND_ENABLED));

        // Load project properties
        Properties properties = new Properties();
        properties.load(D2D2WorldArenaClientMain.class.getClassLoader().getResourceAsStream("project.properties"));
        String projectName = properties.getProperty("project.name");
        String version = properties.getProperty("project.version");
        String defaultGameServer = properties.getProperty("default-game-server");

        log.info(projectName);
        log.info(version);

        String autoEnterPlayerName = CONFIG.getString(ClientConfig.PLAYERNAME);

        D2D2.init(new LWJGLBackend(
                (int) D2D2World.ORIGIN_WIDTH,
                (int) D2D2World.ORIGIN_HEIGHT,
                "(floating) D2D2 World Arena " + autoEnterPlayerName)
        );

        D2D2World.init(false, false);
        D2D2WorldArenaClientAssets.load();

        startVideoMode = GLFWUtils.getVideoMode(MonitorManager.getInstance().getMonitorDeviceId());
        MonitorManager.getInstance().setStartResolution(startVideoMode.getResolution());

        String debugWindowSize = CONFIG.getString(ClientConfig.DEBUG_WINDOW_SIZE);
        if (!debugWindowSize.equals("")) {
            var a = Args.of(debugWindowSize, 'x');
            int width = a.next(int.class);
            int height = a.next(int.class);
            D2D2.getBackend().setSize(width, height);
        }

        String debugWindowXY = CONFIG.getString(ClientConfig.DEBUG_WINDOW_XY);
        if (!debugWindowXY.equals("")) {
            var a = Args.of(debugWindowXY, ',');
            int x = a.next(int.class);
            int y = a.next(int.class);
            D2D2.getBackend().setWindowXY(x, y);
        }

        IntroRoot introRoot = new IntroRoot(projectName + " " + version, defaultGameServer);

        getStage().setRoot(introRoot);

        D2D2.loop();
        exit();
    }

    public static void exit() {
        if (isUnix()) {
            GLFWUtils.linuxCare(MonitorManager.getInstance().getMonitorDeviceId(), startVideoMode);
        }

        Chat.getInstance().saveHistory();
        DebugPanel.saveAll();
        if (GameRoot.INSTANCE != null) GameRoot.INSTANCE.exit();

        log.info("exit");
        System.exit(0);
    }
}
