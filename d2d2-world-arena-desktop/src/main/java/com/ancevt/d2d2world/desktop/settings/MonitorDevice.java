package com.ancevt.d2d2world.desktop.settings;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLVideoModeUtils;
import com.ancevt.util.args.Args;

import java.util.Map;

public class MonitorDevice {

    private static final MonitorDevice instance = new MonitorDevice();

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

    private void apply() {
        Args args = new Args(resolution, "x");
        int w = args.next(int.class);
        int h = args.next(int.class);

        if (fullscreen) {
            D2D2.setFullscreen(true);
            VideoMode videoMode = LWJGLVideoModeUtils.getVideoModes(monitorDeviceId)
                    .stream()
                    .filter(vm -> vm.getWidth() == w && vm.getHeight() == h && vm.getRefreshRate() == 60)
                    .findAny()
                    .orElseThrow();

            LWJGLVideoModeUtils.setVideoMode(monitorDeviceId, D2D2.getStarter().getWindowId(), videoMode);
        } else {
            D2D2.setFullscreen(false);
        }
    }

    @Override
    public String toString() {
        return "MonitorDevice{" +
                "monitorDeviceId=" + monitorDeviceId +
                ", resolution='" + resolution + '\'' +
                ", fullscreen=" + fullscreen +
                ", monitorDeviceName='" + monitorDeviceName + '\'' +
                '}';
    }
}
