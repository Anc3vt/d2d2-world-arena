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
package com.ancevt.d2d2world.gameobject.weapon;

import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IDirectioned;
import com.ancevt.d2d2world.mapkit.MapkitItem;

abstract public class Bullet extends DisplayObjectContainer implements ICollision, IDirectioned {

    private final MapkitItem mapkitItem;
    private boolean collisionEnabled;
    private float collisionX, collisionY, collisionWidth, collisionHeight;
    private Actor owner;
    private int direction;

    public Bullet(MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        setCollisionEnabled(true);
        setCollision(-4, -4, 8, 8);
    }

    public void setDamagingPower(int damagingPower) {
        throw new RuntimeException("can't assign random damaging power to bullet");
    }

    abstract public void prepare();

    abstract public void destroy();

    @Override
    public boolean isSavable() {
        return false;
    }

    @Override
    public int getGameObjectId() {
        return 0;
    }

    @Override
    public MapkitItem getMapkitItem() {
        return null;
    }

    @Override
    public void setCollisionEnabled(boolean b) {
        collisionEnabled = b;
    }

    @Override
    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    @Override
    public void setCollision(float x, float y, float w, float h) {
        collisionX = x;
        collisionY = y;
        collisionWidth = w;
        collisionHeight = h;
    }

    @Override
    public float getCollisionX() {
        return collisionX;
    }

    @Override
    public float getCollisionY() {
        return collisionY;
    }

    @Override
    public float getCollisionWidth() {
        return collisionWidth;
    }

    @Override
    public float getCollisionHeight() {
        return collisionHeight;
    }

    public void setDamagingOwnerActor(Actor actor) {
        owner = actor;
    }

    public Actor getDamagingOwnerActor() {
        return owner;
    }

    @Override
    public void setDirection(int direction) {
        this.direction = direction;
        setScaleX(direction);
    }

    @Override
    public int getDirection() {
        return direction;
    }
}
