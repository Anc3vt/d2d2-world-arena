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
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.*;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

abstract public class Bullet extends DisplayObjectContainer implements ICollision, IDirectioned, ISpeedable, IDamaging, ISynchronized {

    private final MapkitItem mapkitItem;
    private final int gameObjectId;
    private boolean collisionEnabled;
    private float collisionX, collisionY, collisionWidth, collisionHeight;
    private Actor owner;
    private int direction;
    private World world;
    private int damagingPower;
    private float speed;
    private float degree;

    public Bullet(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;
        setCollisionEnabled(true);
    }

    @Override
    public void process() {
        if (isOnWorld()) {
            if (getX() < 0 || getX() > getWorld().getRoom().getWidth() ||
                    getY() < 0 || getY() > getWorld().getRoom().getHeight()) {
                destroy();
            }
        }
    }

    @Override
    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }

    @Property
    public int getOwnerGameObjectId() {
        return owner.getGameObjectId();
    }

    @Override
    public void setDamagingPower(int damagingPower) {
        this.damagingPower = damagingPower;
    }

    @Override
    public int getDamagingPower() {
        return damagingPower;
    }

    abstract public void prepare();

    abstract public void destroy();

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public boolean isSavable() {
        return false;
    }

    @Override
    public int getGameObjectId() {
        return gameObjectId;
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
    public void setCollisionWidth(float collisionWidth) {
        this.collisionWidth = collisionWidth;
    }

    @Override
    public float getCollisionX() {
        return collisionX;
    }

    @Override
    public void setCollisionY(float collisionY) {
        this.collisionY = collisionY;
    }

    @Override
    public float getCollisionY() {
        return collisionY;
    }

    @Override
    public void onCollide(ICollision collideWith) {

    }

    @Override
    public float getCollisionWidth() {
        return collisionWidth;
    }

    @Override
    public void setCollisionHeight(float collisionHeight) {
        this.collisionHeight = collisionHeight;
    }

    @Override
    public float getCollisionHeight() {
        return collisionHeight;
    }

    @Override
    public void setCollisionX(float collisionX) {
        this.collisionX = collisionX;
    }

    @Override
    public void setDamagingOwnerActor(Actor actor) {
        owner = actor;
    }

    @Override
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

    @Property
    public void setDegree(float degree) {
        this.degree = degree;
        setRotation(degree);
    }

    @Property
    public float getDegree() {
        return degree;
    }
}
