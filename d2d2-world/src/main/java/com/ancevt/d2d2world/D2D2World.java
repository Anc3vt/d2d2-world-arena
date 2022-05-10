/*
 *   D2D2 World
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
package com.ancevt.d2d2world;

public class D2D2World {

    public static final float ORIGIN_WIDTH = 800f;
    public static final float ORIGIN_HEIGHT = 600f;

    public static final float SCALE = 1.6f;//2.5f;

    private static boolean server;
    private static boolean editor;

    private D2D2World() {
    }

    public static void init(boolean server, boolean editor) {
        D2D2World.server = server;
        D2D2World.editor = editor;
        D2D2WorldAssets.load();
    }

    public static boolean isServer() {
        return server;
    }

    public static boolean isEditor() {
        return editor;
    }



}
