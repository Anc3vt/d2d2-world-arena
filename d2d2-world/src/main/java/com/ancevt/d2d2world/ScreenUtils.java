package com.ancevt.d2d2world;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ScreenUtils {

    private ScreenUtils() {
    }

    public static @NotNull Dimension getDimension() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return new Dimension(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
    }

    public record Dimension(int width, int height) {
        public int ratio() {
            return width / height;
        }
    }

}
