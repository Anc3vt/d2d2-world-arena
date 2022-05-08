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
package com.ancevt.d2d2world.ui;

import com.ancevt.d2d2.display.Color;

public class HealthBar extends Bar {

    private final float WIDTH = 26;
    private final float HEIGHT = 2f;

    public HealthBar() {
        setSize(WIDTH, HEIGHT);
    }

    @Override
    protected void update() {
        super.update();

        int perc = (int) (getValue() / getMaxValue() * 100);

        if (perc > 75) {
            setForeColor(Color.GREEN);
            setBackColor(Color.DARK_GREEN);
        } else if (perc > 50) {
            setForeColor(Color.YELLOW);
            setBackColor(Color.DARK_YELLOW);
        } else if (perc > 25) {
            setForeColor(Color.of(0x964B00));
            setBackColor(Color.of(0x402000));
        } else {
            setForeColor(Color.RED);
            setBackColor(Color.DARK_RED);
        }
    }
}
