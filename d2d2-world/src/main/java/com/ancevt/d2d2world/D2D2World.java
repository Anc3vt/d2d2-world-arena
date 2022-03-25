/*
 *   D2D2 World
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
package com.ancevt.d2d2world;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.gameobject.*;
import org.jetbrains.annotations.NotNull;

public class D2D2World {

    private static boolean server;
    private static Aim aim;

    private D2D2World() {
    }

    public static void init(boolean server) {
        D2D2World.server = server;
        D2D2.getTextureManager().loadTextureDataInfo("d2d2-world-common-texture-data.inf");
    }

    public static Texture getAimTexture() {
        return D2D2.getTextureManager().getTexture("d2d2-world-common-tileset-aim");
    }

    public static boolean isServer() {
        return server;
    }

    public static void resetGameObjectProperties() {
        IGameObject.worldMap.clear();
        IGameObject.extraMap.clear();
        IGameObject.gameObjectIdMap.clear();
        IMovable.startXMap.clear();
        IMovable.startYMap.clear();
        IMovable.movingSpeedXMap.clear();
        IMovable.movingSpeedYMap.clear();
        ICollision.collisionXMap.clear();
        ICollision.collisionYMap.clear();
        ICollision.collisionWidthMap.clear();
        ICollision.collisionHeightMap.clear();
        ICollision.collisionEnabledMap.clear();
        ISynchronized.permanentSyncMap.clear();
        IControllable.controllerMap.clear();
        ISpeedable.speedMap.clear();
        IRotatable.rotationMap.clear();
    }

    public static Aim getAim() {
        return aim == null ? aim = new Aim() : aim;
    }

    public static class Aim extends DisplayObjectContainer {
        public Aim() {
            Sprite sprite = new Sprite(getAimTexture());
            add(sprite, -sprite.getWidth()/2, -sprite.getHeight()/2);
            setAlpha(0.5f);
        }

        @Override
        public void onEachFrame() {
            //rotate(10f);
            if(getScaleX() > 1.0) {
                toScale(0.9f, 0.9f);
                if(getScaleX() < 0.1) {
                    setScale(1f, 1f);
                }
            }
        }

        public void attack() {
            setScale(2.0f, 2.0f);
        }

        public void setTarget(@NotNull IGameObject gameObject) {
            setXY(gameObject.getX(), gameObject.getY());
        }
    }
}
