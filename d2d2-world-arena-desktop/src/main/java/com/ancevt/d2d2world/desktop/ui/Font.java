/*
 *   D2D2 World Arena Desktop
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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
package com.ancevt.d2d2world.desktop.ui;

import com.ancevt.d2d2.display.text.BitmapFont;

public class Font {

    private static final String BMF_FILE_NAME = "Terminus_Bold_8x16_spaced_shadowed_v1.bmf";
    private static BitmapFont bitmapFont;

    public static BitmapFont getBitmapFont() {
        if (bitmapFont == null) {
            bitmapFont = BitmapFont.loadBitmapFont(BMF_FILE_NAME);
        }
        return bitmapFont;
    }
}
