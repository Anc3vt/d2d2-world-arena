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

import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import org.jetbrains.annotations.NotNull;

abstract public class Weapon {

	private final MapkitItem bulletMapkitItem;
	private Actor owner;

	public Weapon(@NotNull MapkitItem bulletMapkitItem, @NotNull Actor owner) {
		this.bulletMapkitItem = bulletMapkitItem;
		this.owner = owner;
	}

	protected @NotNull MapkitItem getBulletMapkitItem() {
		return bulletMapkitItem;
	}

	public Bullet getNextBullet() {
		return (Bullet) getBulletMapkitItem().createGameObject(getOwner().getWorld().getNextFreeGameObjectId());
	}
	
	public Actor getOwner() {
		return owner;
	}

	public void setOwner(Actor owner) {
		this.owner = owner;
	}
}
