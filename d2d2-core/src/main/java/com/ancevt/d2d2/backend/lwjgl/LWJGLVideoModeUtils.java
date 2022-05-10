/*
 *   D2D2 core
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
package com.ancevt.d2d2.backend.lwjgl;

import com.ancevt.commons.Holder;
import com.ancevt.d2d2.backend.VideoMode;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ancevt.d2d2.backend.lwjgl.OSDetector.isUnix;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;

public class LWJGLVideoModeUtils {

    private static final Map<Long, String> monitorNameMap = new HashMap<>();

    public static @NotNull Map<Long, String> getMonitors() {
        Map<Long, String> monitors = new HashMap();
        PointerBuffer glfwMonitors = glfwGetMonitors();
        for (int i = 0; i < glfwMonitors.limit(); i++) {
            long monitor = glfwMonitors.get(i);
            String name = glfwGetMonitorName(monitor);
            monitors.put(monitor, name);
        }
        return monitors;
    }

    public static long getMonitorByName(String monitorName) {
        Holder<Long> result = new Holder<>(0L);
        getMonitors().forEach((monitor, name) -> {
            if (monitorName.equals(name)) result.setValue(monitor);
        });
        return result.getValue();
    }

    public static @NotNull List<VideoMode> getVideoModes(long monitor) {
        List<VideoMode> videoModes = new ArrayList<>();

        glfwGetVideoModes(monitor).stream().toList().forEach(glfwVidMode -> {
            videoModes.add(
                    VideoMode.builder()
                            .width(glfwVidMode.width())
                            .height(glfwVidMode.height())
                            .refreshRate(glfwVidMode.refreshRate())
                            .build()
            );
        });

        return videoModes;
    }

    public static @NotNull VideoMode getMaxVideoMode(long monitor) {
        var videoModes = getVideoModes(monitor);
        return videoModes.get(videoModes.size() - 1);
    }

    public static long getPrimaryMonitorId() {
        return glfwGetPrimaryMonitor();
    }

    public static VideoMode getVideoMode(long monitor) {
        var glfwVideMode = glfwGetVideoMode(monitor);
        return VideoMode.builder()
                .width(glfwVideMode.width())
                .height(glfwVideMode.height())
                .refreshRate(glfwVideMode.refreshRate())
                .build();
    }

    public static void setVideoMode(long monitor, long windowId, int width, int height, int refreshRate) {
        setVideoMode(monitor, windowId,
                getVideoModes(monitor)
                        .stream()
                        .filter(videoMode -> width == videoMode.getWidth() &&
                                height == videoMode.getHeight() &&
                                (refreshRate == -1 || refreshRate == videoMode.getRefreshRate())
                        )
                        .findAny()
                        .orElseThrow(() -> {
                            throw new IllegalArgumentException("video mode " + width + "x" + height + " " + refreshRate + " not supported");
                        })
        );
    }

    @SneakyThrows
    public static void setVideoMode(long monitor, long windowId, @NotNull VideoMode videoMode) {
        if (isUnix()) {
            linuxCare(monitor, videoMode);

        }

        int width = videoMode.getWidth();
        int height = videoMode.getHeight();

        glfwSetWindowMonitor(
                windowId,
                monitor,
                0,
                0,
                videoMode.getWidth(),
                videoMode.getHeight(),
                videoMode.getRefreshRate()
        );

        //D2D2.getStarter().getRenderer().reshape(width, height);
        //D2D2.getStage().onResize(width, height);
    }

    @SneakyThrows
    public static void linuxCare(long monitor, @NotNull VideoMode videoMode) {
        String monitorName = monitorNameMap.get(monitor);
        if (monitorName == null) {
            monitorName = glfwGetMonitorName(monitor);
            monitorNameMap.put(monitor, monitorName);
        }

        new ProcessBuilder("xrandr",
                "--output",
                "" + monitorName,
                "--mode",
                videoMode.getWidth() + "x" + videoMode.getHeight()
        ).start();
        Thread.sleep(1000);
    }
}