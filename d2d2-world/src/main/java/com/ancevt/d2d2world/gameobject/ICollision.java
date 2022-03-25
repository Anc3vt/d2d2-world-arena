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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

import java.util.HashMap;
import java.util.Map;

public interface ICollision extends IGameObject {

    Map<ICollision, Boolean> collisionEnabledMap = new HashMap<>();
    Map<ICollision, Float> collisionXMap = new HashMap<>();
    Map<ICollision, Float> collisionYMap = new HashMap<>();
    Map<ICollision, Float> collisionWidthMap = new HashMap<>();
    Map<ICollision, Float> collisionHeightMap = new HashMap<>();

    @Property
    default void setCollisionEnabled(boolean value) {
        collisionEnabledMap.put(this, value);
    }

    @Property
    default boolean isCollisionEnabled() {
        return collisionEnabledMap.getOrDefault(this, true);
    }

    default void setCollision(float x, float y, float width, float height) {
        setCollisionX(x);
        setCollisionY(y);
        setCollisionWidth(width);
        setCollisionHeight(height);
    }

    @Property
    default void setCollisionWidth(float collisionWidth) {
        collisionWidthMap.put(this, collisionWidth);
    }

    @Property
    default float getCollisionWidth() {
        return collisionWidthMap.getOrDefault(this, 0f);
    }

    @Property
    default void setCollisionHeight(float collisionHeight) {
        collisionHeightMap.put(this, collisionHeight);
    }

    @Property
    default float getCollisionHeight() {
        return collisionHeightMap.getOrDefault(this, 0f);
    }

    @Property
    default void setCollisionX(float collisionX) {
        collisionXMap.put(this, collisionX);
    }

    @Property
    default float getCollisionX() {
        return collisionXMap.getOrDefault(this, 0f);
    }

    @Property
    default void setCollisionY(float collisionY) {
        collisionYMap.put(this, collisionY);
    }

    @Property
    default float getCollisionY() {
        return collisionYMap.getOrDefault(this, 0f);
    }

    default void onCollide(ICollision collideWith) {

    }

}
