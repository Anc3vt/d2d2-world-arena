
package com.ancevt.d2d2.input;

import com.ancevt.d2d2.D2D2;

public class Mouse {

    private static int x;
    private static int y;

    public static int getX() {
        return x;
    }

    public static int getY() {
        return y;
    }

    public static void setXY(int x, int y) {
        Mouse.x = x;
        Mouse.y = y;
    }

    public static void setVisible(boolean visible) {
        D2D2.getBackend().setMouseVisible(visible);
    }

    public static boolean isVisible() {
        return D2D2.getBackend().isMouseVisible();
    }
}
