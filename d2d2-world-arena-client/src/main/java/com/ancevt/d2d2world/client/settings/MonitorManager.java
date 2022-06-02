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

package com.ancevt.d2d2world.client.settings;

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.GLFWUtils;
import com.ancevt.d2d2.components.dialog.DialogWindow;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.client.scene.intro.IntroScene;
import com.ancevt.util.args.Args;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2.D2D2.stage;
import static com.ancevt.d2d2.backend.lwjgl.OSDetector.isUnix;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;

public class MonitorManager {

    private static final MonitorManager instance = new MonitorManager();
    private String startResolution;
    private DialogWindow dialogWindow;

    public static MonitorManager getInstance() {
        return instance;
    }

    private long monitorDeviceId;

    private String resolution;
    private boolean fullscreen;
    private String safeStoredResolution;
    private boolean safeStoredFullscreen;
    private boolean canceling;

    private MonitorManager() {
    }

    public Map<Long, String> getMonitors() {
        return Map.copyOf(GLFWUtils.getMonitors());
    }

    public long getMonitorIdByWindow() {
        int[] windowWorkArea = GLFWUtils.getWindowInfo();
        int windowCenterX = windowWorkArea[0] + (windowWorkArea[2] / 2); // x + w/2
        int windowCenterY = windowWorkArea[1] + (windowWorkArea[3] / 2); // y + h/2

        Holder<Long> resultHolder = new Holder<>();

        getMonitors().forEach((id, name) -> {
            int[] monitorWorkArea = GLFWUtils.getMonitorInfo(id);
            int x = monitorWorkArea[0];
            int y = monitorWorkArea[1];
            int w = monitorWorkArea[2];
            int h = monitorWorkArea[3];
            if (windowCenterX > x &&
                    windowCenterY > y &&
                    windowCenterX < x + w &&
                    windowCenterY < y + h) {
                resultHolder.setValue(id);
            }
        });

        return resultHolder.getValue() != 0 ? resultHolder.getValue() : getPrimaryMonitorId();
    }

    public void setMonitorDeviceByName(String monitorDeviceName) {
        long monitorDeviceId = getMonitors().keySet()
                .stream()
                .filter(id -> getMonitors().get(id).equals(monitorDeviceName))
                .findAny()
                .orElseThrow();

        setMonitorDeviceId(monitorDeviceId);
    }

    public String getMonitorDeviceName() {
        monitorDeviceId = getMonitorDeviceId();

        Holder<String> monitorNameHolder = new Holder<>();

        var monitors = GLFWUtils.getMonitors();
        monitors.forEach((id, name) -> {
            if (id == monitorDeviceId) monitorNameHolder.setValue(name);
        });

        return monitorNameHolder.getValue();
    }

    public void setMonitorDeviceId(long monitorDeviceId) {
        this.monitorDeviceId = monitorDeviceId;
        apply();
    }

    public void setToPrimaryMonitorDeviceId() {
        setMonitorDeviceId(GLFWUtils.getPrimaryMonitorId());
        apply();
    }

    public long getPrimaryMonitorId() {
        return GLFWUtils.getPrimaryMonitorId();
    }

    public long getMonitorDeviceId() {
        if (monitorDeviceId == 0L) monitorDeviceId = GLFWUtils.getPrimaryMonitorId();
        return monitorDeviceId;
    }

    public void setResolution(@NotNull String resolution) {
        if (resolution.equals(this.resolution)) return;
        safeStoredResolution = getResolution();
        this.resolution = resolution;
    }

    public String getResolution() {
        if (resolution == null) {
            resolution = GLFWUtils.getVideoMode(getMonitorDeviceId()).getResolution();
        }

        return resolution;
    }

    public void setFullscreen(boolean fullscreen) {
        safeStoredFullscreen = isFullscreen();
        this.fullscreen = fullscreen;
        apply();
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setStartResolution(String startResolution) {
        safeStoredResolution = startResolution;
        this.startResolution = startResolution;
    }

    public void rememberResolutionAsStart() {
        String resolution = GLFWUtils.getVideoMode(MonitorManager.getInstance().getMonitorDeviceId()).getResolution();
        setStartResolution(resolution);
    }

    public String getStartResolution() {
        return startResolution;
    }

    private void apply() {
        Args args = Args.of(getResolution(), "x");
        int w = args.next(int.class);
        int h = args.next(int.class);

        if (fullscreen) {
            D2D2.setFullscreen(true);
            VideoMode videoMode = GLFWUtils.getVideoModes(getMonitorDeviceId())
                    .stream()
                    .filter(vm -> vm.getWidth() == w && vm.getHeight() == h && vm.getRefreshRate() == 60)
                    .findAny()
                    .orElseThrow();

            GLFWUtils.setVideoMode(getMonitorDeviceId(), D2D2.getBackend().getWindowId(), videoMode);
            if (isUnix()) {
                GLFWUtils.linuxCare(getMonitorDeviceId(), videoMode);
            }
        } else {
            GLFW.glfwSetWindowMonitor(
                    D2D2.getBackend().getWindowId(),
                    0L,
                    100,
                    100,
                    (int) D2D2World.ORIGIN_WIDTH,
                    (int) D2D2World.ORIGIN_HEIGHT,
                    GLFW_DONT_CARE
            );

            if (isUnix()) {
                Args startResolution = Args.of(getStartResolution(), "x");
                int startResolutionWidth = startResolution.next(int.class);
                int startResolutionHeight = startResolution.next(int.class);
                GLFWUtils.linuxCare(getMonitorDeviceId(),
                        VideoMode.builder()
                                .width(startResolutionWidth)
                                .height(startResolutionHeight)
                                .refreshRate(60)
                                .build());
            }

        }

        if (fullscreen && !canceling && !safeStoredResolution.equals(getResolution())) {
            String message = """
                    Screen video mode changed to %s.
                    Keep this configuration? (%d sec.)""";

            dialogWindow = new DialogWindow();
            dialogWindow.setOnCancelFunction(() -> {
                canceling = true;
                dialogWindow = null;
                MonitorManager.getInstance().setResolution(safeStoredResolution);
                MonitorManager.getInstance().setFullscreen(safeStoredFullscreen);

                if (IntroScene.instance != null) {
                    IntroScene.instance.updateResolutionControls();
                    IntroScene.instance.setControlsEnabled(true);
                }
                canceling = false;
            });

            dialogWindow.setOnOkFunction(() -> {
                dialogWindow = null;
                if (IntroScene.instance != null) {
                    IntroScene.instance.setControlsEnabled(true);
                }
            });

            stage().add(dialogWindow);
            dialogWindow.center();

            if (IntroScene.instance != null) {
                dialogWindow.setXY(
                        (D2D2World.ORIGIN_WIDTH - dialogWindow.getWidth()) / 2,
                        (D2D2World.ORIGIN_HEIGHT - dialogWindow.getHeight()) / 2
                );

                IntroScene.instance.setControlsEnabled(false);
            }

            Async.run(() -> {
                int sec = 10;

                while (sec > 0) {
                    if (dialogWindow == null) break;
                    dialogWindow.setText(message.formatted(getResolution(), sec));
                    sec--;
                    new Lock().lock(1, TimeUnit.SECONDS);
                }
            });
        }
    }

    @Override
    public String toString() {
        return "MonitorDevice{" +
                "monitorDeviceId=" + getMonitorDeviceId() +
                ", monitorDeviceName='" + getMonitorDeviceName() + '\'' +
                ", resolution='" + getResolution() + '\'' +
                ", fullscreen=" + isFullscreen() +
                '}';
    }

}
