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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import static com.ancevt.d2d2.D2D2.getStage;
import static com.ancevt.d2d2.backend.lwjgl.OSDetector.isUnix;
import static com.ancevt.d2d2world.desktop.DesktopConfig.DISPLAY_MONITOR;
import static com.ancevt.d2d2world.desktop.DesktopConfig.DISPLAY_RESOLUTION;
import static com.ancevt.d2d2world.desktop.DesktopConfig.FULLSCREEN;
import static com.ancevt.d2d2world.desktop.DesktopConfig.MODULE_CONFIG;
import static com.ancevt.d2d2world.desktop.DesktopConfig.SOUND_ENABLED;
import static java.lang.Integer.parseInt;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;

@Slf4j
public class D2D2WorldArenaDesktopMain {

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

        // System.setProperty("d2d2.experimental.bloom", "true");

        // DebugPanel.setScale(2f);

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

        MonitorDevice.setMonitorDevice(glfwGetPrimaryMonitor());

        MODULE_CONFIG.ifKeyPresent(DISPLAY_MONITOR, monitorName -> {
            if (monitorName.equals("primary")) {
                MonitorDevice.setMonitorDevice(glfwGetPrimaryMonitor());
            } else {
                long monitor = LWJGLVideoModeUtils.getMonitorByName(monitorName);
                MonitorDevice.setMonitorDevice(monitor);
            }
        });

        VideoMode previousVideoMode = LWJGLVideoModeUtils.getVideoMode(MonitorDevice.getMonitorDevice());

        if (MODULE_CONFIG.getBoolean(FULLSCREEN)) {
            D2D2.setFullscreen(true);

            String displayResolutionString = MODULE_CONFIG.getString(DISPLAY_RESOLUTION);
            if (!displayResolutionString.equals("")) {
                StringTokenizer stringTokenizer = new StringTokenizer(displayResolutionString, "x");
                int w = parseInt(stringTokenizer.nextToken());
                int h = parseInt(stringTokenizer.nextToken());
                LWJGLVideoModeUtils.setVideoMode(
                        MonitorDevice.getMonitorDevice(),
                        D2D2.getStarter().getWindowId(),
                        w, h, -1
                );

            } else {
                for(var videoMode : LWJGLVideoModeUtils.getVideoModes(MonitorDevice.getMonitorDevice())) {
                    if (videoMode.getHeight() == 900 || videoMode.getHeight() == 1050) {
                        LWJGLVideoModeUtils.setVideoMode(MonitorDevice.getMonitorDevice(), D2D2.getStarter().getWindowId(), videoMode);
                        break;
                    }
                }
            }
        }

        String debugScreenSize = MODULE_CONFIG.getString(DesktopConfig.DEBUG_WINDOW_SIZE);
        if (!debugScreenSize.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(debugScreenSize, "x");
            int width = parseInt(tokenizer.nextToken());
            int height = parseInt(tokenizer.nextToken());
            D2D2.getStarter().setSize(width, height);
        }

        String debugWindowLocation = MODULE_CONFIG.getString(DesktopConfig.DEBUG_WINDOW_XY);
        if (!debugWindowLocation.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(debugWindowLocation, ",");
            int x = parseInt(tokenizer.nextToken());
            int y = parseInt(tokenizer.nextToken());
            D2D2.getStarter().setWindowXY(x, y);
        }

        IntroRoot introRoot = new IntroRoot(projectName + " " + version, defaultGameServer);

        getStage().setRoot(introRoot);
        getStage().setScaleMode(ScaleMode.REAL);

        D2D2.loop();

        if (isUnix()) {
            LWJGLVideoModeUtils.linuxCare(MonitorDevice.getMonitorDevice(), previousVideoMode);
        }

        DebugPanel.saveAll();

        if (GameRoot.INSTANCE != null) {
            GameRoot.INSTANCE.exit();
        }

        System.exit(0);
    }
}
