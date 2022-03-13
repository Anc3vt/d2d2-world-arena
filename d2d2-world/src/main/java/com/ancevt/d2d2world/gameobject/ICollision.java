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

public interface ICollision extends IGameObject{

    @Property
	void setCollisionEnabled(boolean value);

    @Property
    boolean isCollisionEnabled();

    void setCollision(float x, float y, float width, float height);

    @Property
    void setCollisionWidth(float collisionWidth);

    @Property
    float getCollisionWidth();

    @Property
    void setCollisionHeight(float collisionHeight);

    @Property
    float getCollisionHeight();

    @Property
    void setCollisionX(float collisionX);

    @Property
    float getCollisionX();

    @Property
    void setCollisionY(float collisionY);

    @Property
    float getCollisionY();

    void onCollide(ICollision collideWith);

}
