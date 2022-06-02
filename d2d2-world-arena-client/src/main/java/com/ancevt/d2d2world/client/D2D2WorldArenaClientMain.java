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

package com.ancevt.d2d2world.client;

import com.ancevt.commons.properties.PropertyWrapper;
import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.GLFWUtils;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.components.ComponentAssets;
import com.ancevt.d2d2.debug.DebugPanel;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.media.SoundSystem;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.client.scene.GameScene;
import com.ancevt.d2d2world.client.scene.intro.IntroScene;
import com.ancevt.d2d2world.client.settings.MonitorManager;
import com.ancevt.d2d2world.client.storage.LocalStorageManager;
import com.ancevt.d2d2world.client.ui.chat.Chat;
import com.ancevt.util.args.Args;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Properties;

import static com.ancevt.d2d2.D2D2.stage;
import static com.ancevt.d2d2.backend.lwjgl.OSDetector.isUnix;
import static com.ancevt.d2d2world.client.config.ClientConfig.CONFIG;
import static com.ancevt.d2d2world.client.config.ClientConfig.DEBUG_WINDOW_SIZE;
import static com.ancevt.d2d2world.client.config.ClientConfig.DEBUG_WINDOW_XY;
import static com.ancevt.d2d2world.client.config.ClientConfig.PLAYERNAME;
import static com.ancevt.d2d2world.client.config.ClientConfig.SOUND_ENABLED;

@Slf4j
public class D2D2WorldArenaClientMain {

    private static VideoMode startVideoMode;

    @SneakyThrows
    public static void main(String @NotNull [] args) throws IOException {
        if(CONFIG.fileExists()) {
            CONFIG.load();
        } else {
            CONFIG.store();
        }
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

        SoundSystem.setEnabled(CONFIG.getBoolean(SOUND_ENABLED, true));

        LocalStorageManager.init();

        // Load project properties
        Properties properties = new Properties();
        properties.load(D2D2WorldArenaClientMain.class.getClassLoader().getResourceAsStream("project.properties"));
        String projectName = properties.getProperty("project.name");
        String version = properties.getProperty("project.version");
        String defaultGameServer = properties.getProperty("default-game-server");

        log.info(projectName);
        log.info(version);

        String autoEnterPlayerName = CONFIG.getProperty(PLAYERNAME);

        Stage stage = D2D2.init(new LWJGLBackend(
                (int) D2D2World.ORIGIN_WIDTH,
                (int) D2D2World.ORIGIN_HEIGHT,
                "(floating) D2D2 World Arena " + autoEnterPlayerName)
        );

        D2D2World.init(false, false);
        ComponentAssets.load();
        D2D2WorldArenaClientAssets.load();

        startVideoMode = GLFWUtils.getVideoMode(MonitorManager.getInstance().getMonitorDeviceId());
        MonitorManager.getInstance().setStartResolution(startVideoMode.getResolution());

        IntroScene introScene = new IntroScene(projectName + " " + version, defaultGameServer);

        stage().add(introScene);

        CONFIG.ifContains(DEBUG_WINDOW_SIZE, value -> {
            var a = Args.of(value, 'x');
            int width = a.next(int.class);
            int height = a.next(int.class);
            D2D2.getBackend().setWindowSize(width, height);
        });

        CONFIG.ifContains(DEBUG_WINDOW_XY, value -> {
            var a = Args.of(value, ',');
            int x = a.next(int.class);
            int y = a.next(int.class);
            D2D2.getBackend().setWindowXY(x, y);
        });

        D2D2.loop();
        exit();
    }

    public static void exit() {
        if (isUnix()) {
            GLFWUtils.linuxCare(MonitorManager.getInstance().getMonitorDeviceId(), startVideoMode);
        }

        Chat.getInstance().saveHistory();
        DebugPanel.saveAll();
        if (GameScene.INSTANCE != null) GameScene.INSTANCE.exit();

        log.info("exit");
        System.exit(0);
    }
}
