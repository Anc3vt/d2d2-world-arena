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

public interface IDamaging extends ICollision {

	@Property
	default void setDamagingPower(int damagingPower) {
		DefaultMaps.damagingPowerMap.put(this, damagingPower);
	}

	@Property
	default int getDamagingPower() {
		return DefaultMaps.damagingPowerMap.getOrDefault(this, 0);
	}

	default void setDamagingOwnerActor(Actor actor) {
		DefaultMaps.damagingOwnerActorMap.put(this, actor);
	}

    default Actor getDamagingOwnerActor() {
		return DefaultMaps.damagingOwnerActorMap.get(this);
	}
}
