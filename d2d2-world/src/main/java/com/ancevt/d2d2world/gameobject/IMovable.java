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

public interface IMovable extends IResettable {

    @Property
    default void setStartX(float x) {
        DefaultMaps.startXMap.put(this, x);
    }

    @Property
    default void setStartY(float y) {
        DefaultMaps.startYMap.put(this, y);
    }

    default void setStartXY(float x, float y) {
        setStartX(x);
        setStartY(y);
    }

    @Property
    default float getStartX() {
        return DefaultMaps.startXMap.getOrDefault(this, 0f);
    }

    @Property
    default float getStartY() {
        return DefaultMaps.startYMap.getOrDefault(this, 0f);
    }

    default float getMovingSpeedX() {
        return DefaultMaps.movingSpeedXMap.getOrDefault(this, 0f);
    }

    default float getMovingSpeedY() {
        return DefaultMaps.movingSpeedYMap.getOrDefault(this, 0f);
    }

    default void setMovingSpeedX(float value) {
        DefaultMaps.movingSpeedXMap.put(this, value);
    }

    default void setMovingSpeedY(float value) {
        DefaultMaps.movingSpeedYMap.put(this, value);
    }
}
