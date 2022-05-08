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

import com.ancevt.d2d2.display.texture.Texture;

import static com.ancevt.d2d2.D2D2.getTextureManager;

public class D2D2WorldAssets {

    public static void load() {
        getTextureManager().loadTextureDataInfo("d2d2-world.inf");
    }

    public static Texture getPickupBubbleTexture32() {
        return getTextureManager().getTexture("d2d2-world-pickup-bubble-32px");
    }

    public static Texture getPickupBubbleTexture16() {
        return getTextureManager().getTexture("d2d2-world-pickup-bubble-16px");
    }

    public static Texture getRopeTexture() {
        return getTextureManager().getTexture("d2d2-world-rope");
    }

    public static Texture getWaterBubbleTexture() {
        return getTextureManager().getTexture("d2d2-world-water-bubble");
    }
}
