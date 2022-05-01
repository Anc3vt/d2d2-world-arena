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
package com.ancevt.d2d2world.desktop.scene.intro;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;

public class CityBgSprite extends Sprite {

    private static final float SPEED = 10;
    private static Texture texture;

    public static Texture createOrGetTexture() {
        if (texture == null) {
            texture = D2D2.getTextureManager().loadTextureAtlas("city-bg.png").createTexture();
        }
        return texture;
    }

    public CityBgSprite() {
        super(createOrGetTexture());
        setColor(Color.BLACK);
        setRepeatX(3);
        setScale(2f,2f);
    }

    @Override
    public void onEachFrame() {
        moveX(-SPEED);
        if(getX() < -getTexture().width() * getScaleX()) {
            setX(0);
        }
    }
}
