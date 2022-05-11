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
package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.backend.lwjgl.LWJGLVideoModeUtils;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2world.client.D2D2WorldArenaDesktopAssets;
import com.ancevt.d2d2world.client.settings.MonitorDevice;

public class ResolutionChooser extends Chooser<VideoMode> {

    public static final String WINDOWED = "windowed";

    public ResolutionChooser() {
        setWidth(180f);

        addItem(WINDOWED, null);

        LWJGLVideoModeUtils.getVideoModes(MonitorDevice.getInstance().getMonitorDeviceId()).forEach(videoMode -> {
            if (videoMode.getRefreshRate() != 60 || videoMode.getHeight() < 600) return;

            String key = videoMode.getWidth() + "x" + videoMode.getHeight();
            addItem(key, videoMode);
        });
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2WorldArenaDesktopAssets.load();

        var startVideoMode = LWJGLVideoModeUtils.getVideoMode(MonitorDevice.getInstance().getMonitorDeviceId());
        MonitorDevice.getInstance().setStartResolution(startVideoMode.getResolution());

        ResolutionChooser resolutionChooser = new ResolutionChooser();
        root.add(resolutionChooser);
        resolutionChooser.addEventListener(ChooserEvent.CHOOSER_APPLY, event -> {
            VideoMode videoMode = resolutionChooser.getSelectedItem();

            if (videoMode == null) {
                MonitorDevice.getInstance().setFullscreen(false);
                return;
            } else {
                MonitorDevice.getInstance().setResolution(videoMode.getResolution());
                MonitorDevice.getInstance().setFullscreen(true);
            }

            System.out.println(videoMode);
        });
        D2D2.loop();

        LWJGLVideoModeUtils.linuxCare(MonitorDevice.getInstance().getMonitorDeviceId(), startVideoMode);
    }
}
