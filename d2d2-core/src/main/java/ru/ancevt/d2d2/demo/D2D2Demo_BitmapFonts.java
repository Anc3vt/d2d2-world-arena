/*
 *   D2D2 core
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
package ru.ancevt.d2d2.demo;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.debug.FpsMeter;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.text.BitmapFont;
import ru.ancevt.d2d2.display.text.BitmapText;
import ru.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

public class D2D2Demo_BitmapFonts {

    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, D2D2Demo_BitmapFonts.class.getName()));
        Root root = D2D2.getStage().getRoot();

        BitmapFont font2 = BitmapFont.loadBitmapFont("PressStart2P.bmf");
        BitmapText bitmapText2 = new BitmapText(font2);
        bitmapText2.setText("PRESSSTART.bmf`` алалала");

        BitmapFont.setDefaultBitmapFont(font2);

        root.add(bitmapText2, 0, 100);

        //bitmapText2.setScale(2,2);

        root.add(new FpsMeter());

        D2D2.loop();
    }
}
