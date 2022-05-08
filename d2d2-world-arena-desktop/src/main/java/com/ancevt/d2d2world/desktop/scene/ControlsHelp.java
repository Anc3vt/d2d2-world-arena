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
package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.desktop.D2D2WorldArenaDesktopAssets;

public class ControlsHelp extends Sprite {

    private static final int SPEED = 25;

    private int direction = SPEED;

    private int tact;

    public ControlsHelp() {
        super(D2D2WorldArenaDesktopAssets.getControlsHelpTexture());
        setColor(new Color(0xFF, 0x80, 0xFF));
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();

        int value = getColor().getB();

        getColor().setB(value + direction);

        value = getColor().getB();

        if (value > 0xFF) {
            direction = -SPEED;
        } else if (value < 0) {
            direction = SPEED;
        }

        tact++;

        if(tact > 250) {
            setAlpha(getAlpha() - 0.075f);
            if(getAlpha() <= 0.01f) {
                removeFromParent();
            }
        }
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating"));
        D2D2World.init(false, false);
        root.setBackgroundColor(Color.DARK_GRAY);

        root.add(new ControlsHelp(), 0, 0);
        root.setScale(3f, 3f);

        D2D2.loop();
    }
}
