
package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.backend.lwjgl.GLFWUtils;
import com.ancevt.d2d2.display.Root;
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
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2WorldArenaClientAssets.load();

        var startVideoMode = GLFWUtils.getVideoMode(MonitorManager.getInstance().getMonitorDeviceId());
        MonitorManager.getInstance().setStartResolution(startVideoMode.getResolution());

        ResolutionChooser resolutionChooser = new ResolutionChooser();
        root.add(resolutionChooser);
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
