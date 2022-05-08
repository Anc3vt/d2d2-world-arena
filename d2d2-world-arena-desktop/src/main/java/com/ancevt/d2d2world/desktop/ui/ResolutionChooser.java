package com.ancevt.d2d2world.desktop.ui;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.VideoMode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLStarter;
import com.ancevt.d2d2.backend.lwjgl.LWJGLVideoModeUtils;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2world.desktop.D2D2WorldArenaDesktopAssets;
import com.ancevt.d2d2world.desktop.settings.MonitorDevice;

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
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
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
