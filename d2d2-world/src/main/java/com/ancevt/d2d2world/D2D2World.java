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

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.gameobject.IGameObject;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2.D2D2.getTextureManager;

public class D2D2World {

    private static boolean server;
    private static Aim aim;
    private static boolean editor;

    private D2D2World() {
    }

    public static void init(boolean server, boolean editor) {
        D2D2World.server = server;
        D2D2World.editor = editor;
        getTextureManager().loadTextureDataInfo("d2d2-world-common-texture-data.inf");
    }

    public static Texture getControlsHelpTexture() {
        return getTextureManager().getTexture("d2d2-world-common-tileset-controls-help");
    }

    public static Texture getAimTexture() {
        return getTextureManager().getTexture("d2d2-world-common-tileset-aim");
    }

    public static Texture getSpawnEffectTexture() {
        return getTextureManager().getTexture("d2d2-world-common-tileset-spawn-effect");
    }

    public static Texture getWaterBubbleTexture() {
        return getTextureManager().getTexture("d2d2-world-common-tileset-water-bubble");
    }

    public static Texture getRopeTexture() {
        return getTextureManager().getTexture("d2d2-world-common-tileset-rope");
    }

    public static Texture getPickupBubbleTexture32() {
        return getTextureManager().getTexture("pickup-bubble-32");
    }

    public static Texture getPickupBubbleTexture16() {
        return getTextureManager().getTexture("pickup-bubble-16");
    }

    public static boolean isServer() {
        return server;
    }

    public static boolean isEditor() {
        return editor;
    }

    public static Aim getAim() {
        return aim == null ? aim = new Aim() : aim;
    }

    public static class Aim extends DisplayObjectContainer {
        private final Sprite sprite;

        public Aim() {
            sprite = new Sprite(getAimTexture());
            add(sprite, -sprite.getWidth()/2, -sprite.getHeight()/2 - 3);
        }

        @Override
        public void onEachFrame() {
            if(getScaleX() > 1.0) {
                toScale(0.9f, 0.9f);
                if(getScaleX() < 0.1) {
                    setScale(1f, 1f);
                }
            }
        }

        public void setColor(Color color) {
            sprite.setColor(color);
        }

        public Color getColor() {
            return sprite.getColor();
        }

        public void attack() {
            setScale(2.0f, 2.0f);
        }

        public void setTarget(@NotNull IGameObject gameObject) {
            setXY(gameObject.getX(), gameObject.getY());
        }
    }
}
