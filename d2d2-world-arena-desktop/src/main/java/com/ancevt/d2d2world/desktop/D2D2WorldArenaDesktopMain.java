/*
 *   D2D2 World Arena Desktop
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
package com.ancevt.d2d2world.desktop;

import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLStarter;
import com.ancevt.d2d2.backend.lwjgl.LWJGLVideoModeUtils;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.media.SoundSystem;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.debug.DebugPanel;
import com.ancevt.d2d2world.desktop.scene.GameRoot;
import com.ancevt.d2d2world.desktop.scene.intro.IntroRoot;
import com.ancevt.d2d2world.desktop.settings.DesktopConfig;
import com.ancevt.d2d2world.desktop.settings.MonitorDevice;
import com.ancevt.d2d2world.desktop.ui.chat.Chat;
import com.ancevt.util.args.Args;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

import static com.ancevt.d2d2.D2D2.getStage;
import static com.ancevt.d2d2.backend.lwjgl.OSDetector.isUnix;
import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.DISPLAY_RESOLUTION;
import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.FULLSCREEN;
import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.MODULE_CONFIG;
import static com.ancevt.d2d2world.desktop.settings.DesktopConfig.SOUND_ENABLED;

@Slf4j
public class D2D2WorldArenaDesktopMain {

    private static VideoMode previousVideoMode;

    @SneakyThrows
    public static void main(String[] args) throws IOException {
        MODULE_CONFIG.load();
        for (String arg : args) {
            if (arg.startsWith("-P")) {
                arg = arg.substring(2);
                String[] split = arg.split("=");
                String key = split[0];
                String value = split[1];
                MODULE_CONFIG.setProperty(key, value);
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

        SoundSystem.setEnabled(MODULE_CONFIG.getBoolean(SOUND_ENABLED));

        // Load project properties
        Properties properties = new Properties();
        properties.load(D2D2WorldArenaDesktopMain.class.getClassLoader().getResourceAsStream("project.properties"));
        String projectName = properties.getProperty("project.name");
        String version = properties.getProperty("project.version");
        String defaultGameServer = properties.getProperty("default-game-server");

        log.info(projectName);
        log.info(version);

        String autoEnterPlayerName = MODULE_CONFIG.getString(DesktopConfig.PLAYERNAME);

        D2D2.init(new LWJGLStarter(
                (int) D2D2World.ORIGIN_WIDTH,
                (int) D2D2World.ORIGIN_HEIGHT,
                "(floating) D2D2 World Arena " + autoEnterPlayerName)
        );

        D2D2World.init(false, false);
        D2D2WorldArenaDesktopAssets.load();

        previousVideoMode = LWJGLVideoModeUtils.getVideoMode(MonitorDevice.getInstance().getMonitorDeviceId());
        MonitorDevice.getInstance().setStartResolution(previousVideoMode.getResolution());

        String displayResolutionString = MODULE_CONFIG.getString(DISPLAY_RESOLUTION);
        if (!displayResolutionString.equals("")) {
            MonitorDevice.getInstance().setResolution(displayResolutionString);
        } else {
            for (var videoMode : LWJGLVideoModeUtils.getVideoModes(MonitorDevice.getInstance().getMonitorDeviceId())) {
                if (videoMode.getRefreshRate() == 60) {
                    if (videoMode.getHeight() == 900 || videoMode.getHeight() == 1050) {
                        MonitorDevice.getInstance().setResolution(videoMode.getResolution());
                        break;
                    }
                }
            }
        }

        if (MODULE_CONFIG.getBoolean(FULLSCREEN)) {
            MonitorDevice.getInstance().setFullscreen(true);
        }

        String debugWindowSize = MODULE_CONFIG.getString(DesktopConfig.DEBUG_WINDOW_SIZE);
        if (!debugWindowSize.equals("")) {
            var a = new Args(debugWindowSize, 'x');
            int width = a.next(int.class);
            int height = a.next(int.class);
            D2D2.getStarter().setSize(width, height);
        }

        String debugWindowXY = MODULE_CONFIG.getString(DesktopConfig.DEBUG_WINDOW_XY);
        if (!debugWindowXY.equals("")) {
            var a = new Args(debugWindowXY, ',');
            int x = a.next(int.class);
            int y = a.next(int.class);
            D2D2.getStarter().setWindowXY(x, y);
        }

        IntroRoot introRoot = new IntroRoot(projectName + " " + version, defaultGameServer);

        getStage().setRoot(introRoot);
        getStage().setScaleMode(ScaleMode.REAL);

        D2D2.loop();
        exit();
    }

    public static void exit() {
        if (isUnix()) {
            LWJGLVideoModeUtils.linuxCare(MonitorDevice.getInstance().getMonitorDeviceId(), previousVideoMode);
        }

        Chat.getInstance().saveHistory();
        DebugPanel.saveAll();
        if (GameRoot.INSTANCE != null) GameRoot.INSTANCE.exit();

        log.info("exit");
        System.exit(0);
    }
}
