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
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class ShadowRadial extends DisplayObjectContainer {

    private static Texture texture;
    private final List<Sprite> sprites;
    private Color color = Color.WHITE;

    public ShadowRadial() {
        if (texture == null) {
            texture = textureManager().loadTextureAtlas("shadow-radial.png").createTexture();
        }
        sprites = new ArrayList<>();
        setValue(1);
    }

    public void setValue(int value) {
        while (!sprites.isEmpty()) {
            sprites.remove(0).removeFromParent();
        }

        for (int i = 0; i < value; i++) {
            Sprite sprite = new Sprite(texture);
            sprite.setColor(color);
            float sw = D2D2.getStage().getStageWidth();
            float sh = D2D2.getStage().getStageHeight();

            float scaleX = sw / sprite.getWidth();
            float scaleY = sh / sprite.getHeight();
            sprite.setScale(scaleX, scaleY);
            add(sprite, (-sprite.getWidth() * scaleX) / 2, (-sprite.getHeight() * scaleY) / 2);

            sprites.add(sprite);
        }
    }

    public int getValue() {
        return sprites.size();
    }

    public void setColor(Color color) {
        this.color = color;
        sprites.forEach(s -> s.setColor(color));
    }

    public Color getColor() {
        return sprites.get(0).getColor();
    }

    @Override
    public void onEachFrame() {

    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating"));
        root.setBackgroundColor(Color.WHITE);

        ShadowRadial shadowRadial = new ShadowRadial();
        shadowRadial.setColor(Color.BLACK);


        root.add(shadowRadial);
        D2D2.loop();
    }
}
