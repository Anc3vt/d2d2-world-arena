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

public interface ITight extends ICollision {

    Map<ITight, Boolean> floorOnlyMap = new HashMap<>();
    Map<ITight, Boolean> pushableMap = new HashMap<>();

    @Property
    default void setFloorOnly(boolean b) {
        floorOnlyMap.put(this, b);
    }

    @Property
    default boolean isFloorOnly() {
        return floorOnlyMap.getOrDefault(this, false);
    }

    @Property
    default void setPushable(boolean b) {
        pushableMap.put(this, b);
    }

    @Property
    default boolean isPushable() {
        return pushableMap.getOrDefault(this, false);
    }
}
