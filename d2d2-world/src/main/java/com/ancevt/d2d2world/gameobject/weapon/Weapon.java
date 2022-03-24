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

import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

abstract public class Weapon {

	private final MapkitItem bulletMapkitItem;
	private final IDisplayObject displayObject;

	public Weapon(@NotNull MapkitItem bulletMapkitItem, @NotNull Actor owner, @NotNull IDisplayObject displayObject) {
		this.bulletMapkitItem = bulletMapkitItem;
		this.owner = owner;
		this.displayObject = displayObject;
	}

	private Actor owner;

	protected @NotNull MapkitItem getBulletMapkitItem() {
		return bulletMapkitItem;
	}

	abstract public int getAttackTime();

	public IDisplayObject getDisplayObject() {
		return displayObject;
	}

	public Bullet getNextBullet(float degree) {
		Bullet bullet = (Bullet) getBulletMapkitItem().createGameObject(getOwner().getWorld().getNextFreeGameObjectId());
		bullet.setDegree(degree);
		return bullet;
	}

	abstract public void shoot(@NotNull World world);

	abstract public void playShootSound();

	abstract public void playBulletDestroySound();

	public Actor getOwner() {
		return owner;
	}

	public void setOwner(@NotNull Actor owner) {
		this.owner = owner;
	}
}
