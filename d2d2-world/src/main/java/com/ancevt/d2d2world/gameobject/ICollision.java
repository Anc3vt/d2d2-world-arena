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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface ICollision extends IGameObject {

    @Property
    default void setCollisionEnabled(boolean value) {
        DefaultMaps.collisionEnabledMap.put(this, value);
    }

    @Property
    default boolean isCollisionEnabled() {
        return DefaultMaps.collisionEnabledMap.getOrDefault(this, true);
    }

    default void setCollision(float x, float y, float width, float height) {
        setCollisionX(x);
        setCollisionY(y);
        setCollisionWidth(width);
        setCollisionHeight(height);
    }

    @Property
    default void setCollisionWidth(float collisionWidth) {
        DefaultMaps.collisionWidthMap.put(this, collisionWidth);
    }

    @Property
    default float getCollisionWidth() {
        return DefaultMaps.collisionWidthMap.getOrDefault(this, 0f);
    }

    @Property
    default void setCollisionHeight(float collisionHeight) {
        DefaultMaps.collisionHeightMap.put(this, collisionHeight);
    }

    @Property
    default float getCollisionHeight() {
        return DefaultMaps.collisionHeightMap.getOrDefault(this, 0f);
    }

    @Property
    default void setCollisionX(float collisionX) {
        DefaultMaps.collisionXMap.put(this, collisionX);
    }

    @Property
    default float getCollisionX() {
        return DefaultMaps.collisionXMap.getOrDefault(this, 0f);
    }

    @Property
    default void setCollisionY(float collisionY) {
        DefaultMaps.collisionYMap.put(this, collisionY);
    }

    @Property
    default float getCollisionY() {
        return DefaultMaps.collisionYMap.getOrDefault(this, 0f);
    }

    default void onCollide(ICollision collideWith) {

    }

}
