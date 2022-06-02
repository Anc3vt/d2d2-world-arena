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

package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.GLFWUtils;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.components.Chooser;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;
import com.ancevt.d2d2world.client.settings.MonitorManager;

public class ResolutionChooser extends Chooser<VideoMode> {

    public static final String WINDOWED = "windowed";

    public ResolutionChooser() {
        setWidth(180f);
        fill();
    }

    public void fill() {
        clear();

        addItem(WINDOWED, null);

        GLFWUtils.getVideoModes(MonitorManager.getInstance().getMonitorDeviceId()).forEach(videoMode -> {
            if (videoMode.getRefreshRate() != 60 || videoMode.getHeight() < 600) return;

            String key = videoMode.getWidth() + "x" + videoMode.getHeight();
            addItem(key, videoMode);
        });
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2WorldArenaClientAssets.load();

        var startVideoMode = GLFWUtils.getVideoMode(MonitorManager.getInstance().getMonitorDeviceId());
        MonitorManager.getInstance().setStartResolution(startVideoMode.getResolution());

        ResolutionChooser resolutionChooser = new ResolutionChooser();
        stage.add(resolutionChooser);
        resolutionChooser.addEventListener(ChooserEvent.CHOOSER_APPLY, event -> {
            VideoMode videoMode = resolutionChooser.getSelectedItemObject();

            if (videoMode == null) {
                MonitorManager.getInstance().setFullscreen(false);
                return;
            } else {
                MonitorManager.getInstance().setResolution(videoMode.getResolution());
                MonitorManager.getInstance().setFullscreen(true);
            }

            System.out.println(videoMode);
        });
        D2D2.loop();

        GLFWUtils.linuxCare(MonitorManager.getInstance().getMonitorDeviceId(), startVideoMode);
    }
}
