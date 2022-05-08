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

import com.ancevt.d2d2.display.*;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.desktop.D2D2WorldArenaDesktopAssets;
import org.jetbrains.annotations.NotNull;

public class SpawnEffect extends DisplayObjectContainer {

    private float speed1 = +8.0f;
    private float speed2 = -4.0f;

    private final Sprite sprite1;
    private final Sprite sprite2;

    private final Texture texture;

    private final int textureX;
    private final int textureY;

    private float y1;
    private float y2;

    private int time;

    public SpawnEffect() {
        texture = D2D2WorldArenaDesktopAssets.getSpawnEffectTexture();

        textureX = texture.x();
        textureY = texture.y();

        sprite1 = new Sprite();
        sprite2 = new Sprite();

        add(sprite1, -texture.width() / 2f, -texture.height() / 2f);
        add(sprite2, -texture.width() / 2f, -texture.height() / 2f);

        setAlpha(0.0f);

        setScaleY(1.5f);
    }

    public void setColor1(Color color) {
        sprite1.setColor(color);
    }

    public Color getColor1() {
        return sprite1.getColor();
    }

    public void setColor2(Color color) {
        sprite2.setColor(color);
    }

    public Color getColor2() {
        return sprite2.getColor();
    }

    @Override
    public void onEachFrame() {
        time++;

        y1 += speed1;
        y2 += speed2;

        sprite1.setTexture(texture.getSubtexture(0, (int) y1, texture.width(), 32));
        sprite2.setTexture(texture.getSubtexture(0, (int) y2, texture.width(), 32));

        if (y1 < 0) {
            y1 = texture.height() - 32;
        } else if (y1 > texture.height() - 32) {
            y1 = 0;
        }

        if (y2 < 0) {
            y2 = texture.height() - 32;
        } else if (y2 > texture.height() - 32) {
            y2 = 0;
        }

        if (time < 10 && getAlpha() < 1f) {
            setAlpha(getAlpha() + 0.05f);
        }

        if (time > 40 && getAlpha() > 0) {
            setAlpha(getAlpha() - 0.05f);
        }

        if (getAlpha() <= 0) removeFromParent();
    }

    public static void doSpawnEffect(float x, float y, @NotNull IDisplayObjectContainer targetParent) {
        targetParent.add(new SpawnEffect(), x, y + 48);
    }

}
