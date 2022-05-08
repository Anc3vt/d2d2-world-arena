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
