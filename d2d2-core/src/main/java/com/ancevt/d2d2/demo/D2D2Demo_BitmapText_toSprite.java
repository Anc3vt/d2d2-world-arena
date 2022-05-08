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
package com.ancevt.d2d2.demo;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;

public class D2D2Demo_BitmapText_toSprite {
    public static void main(String[] args) {
        D2D2.init(new LWJGLBackend(800, 600, "D2D2Demo2"));
        Root root = new Root();

        BitmapText bitmapText = new BitmapText();
        bitmapText.setText("JAVA: How to create png image from BufferedImage. Image ...");

        Sprite sprite = bitmapText.toSprite();

        sprite.setScale(2,2);
        root.add(sprite, 10, 20);

        D2D2.getTextureManager().bitmapTextToTextureAtlas(bitmapText);

        root.add(new FpsMeter());
        D2D2.getStage().setRoot(root);
        D2D2.loop();
    }
}
