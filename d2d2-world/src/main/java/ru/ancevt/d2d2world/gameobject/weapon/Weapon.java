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
package ru.ancevt.d2d2world.gameobject.weapon;

import ru.ancevt.d2d2world.gameobject.Actor;

abstract public class Weapon {

	protected static final int BULLET_PULL_SIZE = 10;
	
	public static final int TYPE_DEFAULT = 0;
	
	private final Bullet[] bulletPull;
	private Actor owner;
	private int bulletIterator;
	
	public Weapon() {
		bulletPull = createPull();
	}
	
	protected Bullet[] createPull() {
		final Bullet[] result = new Bullet[BULLET_PULL_SIZE];
		for(int i = 0; i < result.length; i ++) {
			result[i] = createBullet();
		}
		
		return result;
	}
	
	abstract protected Bullet createBullet();
	
	public Bullet getNextBullet() {
		final Bullet result = bulletPull[bulletIterator];
		result.prepare();
		
		bulletIterator++;
		if(bulletIterator >= bulletPull.length)
			bulletIterator = 0;
		
		return result;
	}
	
	
	public static Weapon createWeapon(final int weaponTypeId) {
		return null;
	}

	public Actor getOwner() {
		return owner;
	}

	public void setOwner(Actor owner) {
		this.owner = owner;
		for (Bullet bullet : bulletPull) bullet.setDamagingOwnerActor(owner);
	}
}
