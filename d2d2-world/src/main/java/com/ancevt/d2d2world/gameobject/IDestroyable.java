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

public interface IDestroyable extends ICollision, IResettable {

    @Property
    default void setMaxHealth(int health) {
        DefaultMaps.maxHealthsMap.put(this, health);
    }

    @Property
    default int getMaxHealth() {
        return DefaultMaps.maxHealthsMap.getOrDefault(this, 0);
    }

    @Property
    default void setHealth(int health) {
        if(health < 0) {
            health = 0;
        } else if(health > getMaxHealth()) {
            health = getMaxHealth();
        }

        DefaultMaps.healthMap.put(this, health);
    }

    @Property
    default int getHealth() {
        return DefaultMaps.healthMap.getOrDefault(this, 0);
    }

    void repair();

    void setHealthBy(int health, IDamaging damaging, boolean fromServer);

    void damage(int toHealth, IDamaging damaging);
}
