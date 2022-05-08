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
package com.ancevt.d2d2world.desktop.settings;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.backend.lwjgl.LWJGLVideoModeUtils;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.util.args.Args;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

import static com.ancevt.d2d2.backend.lwjgl.OSDetector.isUnix;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;

public class MonitorDevice {

    private static final MonitorDevice instance = new MonitorDevice();
    private String startResolution;

    public static MonitorDevice getInstance() {
        return instance;
    }

    private long monitorDeviceId;

    private String resolution;
    private boolean fullscreen;
    private String monitorDeviceName;

    private MonitorDevice() {
    }

    public void setMonitorDeviceByName(String monitorDeviceName) {
        this.monitorDeviceName = monitorDeviceName;

        Map<Long, String> monitors = LWJGLVideoModeUtils.getMonitors();

        long monitorDeviceId = monitors.keySet()
                .stream()
                .filter(id -> monitors.get(id).equals(monitorDeviceName))
                .findAny()
                .orElseThrow();

        setMonitorDeviceId(monitorDeviceId);
    }

    public String getMonitorDeviceName() {
        return monitorDeviceName;
    }

    public void setMonitorDeviceId(long monitorDeviceId) {
        this.monitorDeviceId = monitorDeviceId;
        apply();
    }

    public long getMonitorDeviceId() {
        if (monitorDeviceId == 0L) monitorDeviceId = LWJGLVideoModeUtils.getPrimaryMonitorId();
        return monitorDeviceId;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
        apply();
    }

    public String getResolution() {
        return resolution;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        apply();
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setStartResolution(String startResolution) {
        this.startResolution = startResolution;
    }

    public String getStartResolution() {
        return startResolution;
    }

    private void apply() {
        Args args = new Args(resolution, "x");
        int w = args.next(int.class);
        int h = args.next(int.class);

        if (fullscreen) {
            D2D2.setFullscreen(true);
            VideoMode videoMode = LWJGLVideoModeUtils.getVideoModes(getMonitorDeviceId())
                    .stream()
                    .filter(vm -> vm.getWidth() == w && vm.getHeight() == h && vm.getRefreshRate() == 60)
                    .findAny()
                    .orElseThrow();

            LWJGLVideoModeUtils.setVideoMode(getMonitorDeviceId(), D2D2.getBackend().getWindowId(), videoMode);
            LWJGLVideoModeUtils.linuxCare(getMonitorDeviceId(), videoMode);
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
                Args startResolutionArgs = new Args(getStartResolution(), "x");
                int startResolutionWidth = startResolutionArgs.next(int.class);
                int startResolutionHeight = startResolutionArgs.next(int.class);
                LWJGLVideoModeUtils.linuxCare(getMonitorDeviceId(),
                        VideoMode.builder()
                                .width(startResolutionWidth)
                                .height(startResolutionHeight)
                                .refreshRate(60)
                                .build());
            }

        }
    }

    @Override
    public String toString() {
        return "MonitorDevice{" +
                "monitorDeviceId=" + getMonitorDeviceId() +
                ", monitorDeviceName='" + monitorDeviceName + '\'' +
                ", resolution='" + resolution + '\'' +
                ", fullscreen=" + fullscreen +
                '}';
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating"));
        D2D2World.init(true, true);









        D2D2.loop();
    }
}



































