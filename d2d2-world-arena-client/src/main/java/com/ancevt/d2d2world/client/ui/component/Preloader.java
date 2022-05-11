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
package com.ancevt.d2d2world.client.ui.component;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.client.D2D2WorldArenaDesktopAssets;

public class Preloader extends DisplayObjectContainer {

    private static final int FRAMES_PER_ROTATE = 10;
    private int timer = FRAMES_PER_ROTATE;

    public Preloader() {
        add(new Sprite(D2D2WorldArenaDesktopAssets.getPreloaderTexture()), -32, -32);
    }

    @Override
    public void onEachFrame() {
        if (timer-- <= 0) {
            timer = FRAMES_PER_ROTATE;
            rotate(45);
        }
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2.setSmoothMode(true);
        D2D2World.init(false, false);
        root.add(new Preloader(), 100, 100);
        D2D2.loop();
    }
}