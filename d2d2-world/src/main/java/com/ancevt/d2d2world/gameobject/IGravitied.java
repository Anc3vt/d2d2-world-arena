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

public interface IGravitied extends IMovable {

    @Property
    default float getWeight() {
        return DefaultMaps.weightMap.getOrDefault(this, 0f);
    }

    @Property
    default void setWeight(float weight) {
        DefaultMaps.weightMap.put(this, weight);
    }

    default void setFloor(ICollision floor) {
        DefaultMaps.floorMap.put(this, floor);
    }

    default ICollision getFloor() {
        return DefaultMaps.floorMap.get(this);
    }

    default void setVelocityX(float velocityX) {
        DefaultMaps.velocityXMap.put(this, velocityX);
    }

    default void setVelocityY(float velocityY) {
        DefaultMaps.velocityYMap.put(this, velocityY);
    }

    default void setVelocity(float vX, float vY) {
        setVelocityX(vX);
        setVelocityY(vY);
    }

    default float getVelocityX() {
        return DefaultMaps.velocityXMap.getOrDefault(this, 0f);
    }

    default float getVelocityY() {
        return DefaultMaps.velocityYMap.getOrDefault(this, 0f);
    }

    @Property
    default void setGravityEnabled(boolean b) {
        DefaultMaps.gravityEnabledMap.put(this, b);
    }

    @Property
    default boolean isGravityEnabled() {
        return DefaultMaps.gravityEnabledMap.getOrDefault(this, false);
    }
}
