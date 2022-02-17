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
package ru.ancevt.d2d2world.gameobject;

import ru.ancevt.d2d2world.data.Property;

public interface IGravitied extends IMovable {

    @Property
    float getWeight();

    @Property
    void setWeight(float weight);

    void setFloor(final ICollision floor);

    ICollision getFloor();

    void setVelocityX(float velocityX);

    void setVelocityY(float velocityY);

    void setVelocity(float vX, float vY);

    float getVelocityX();

    float getVelocityY();

    @Property
    void setGravityEnabled(boolean b);

    @Property
    boolean isGravityEnabled();
}
