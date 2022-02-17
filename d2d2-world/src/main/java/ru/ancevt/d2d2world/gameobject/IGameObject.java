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

import ru.ancevt.d2d2.display.IDisplayObject;
import ru.ancevt.d2d2world.map.mapkit.MapkitItem;
import ru.ancevt.d2d2world.data.Property;
import ru.ancevt.d2d2world.world.World;

public interface IGameObject extends IDisplayObject, IProcessable {

    int getGameObjectId();

    @Property
    String getName();

    @Property
    void setName(String name);

    @Property
    void setX(float x);

    @Property
    float getX();

    @Property
    void setY(float y);

    @Property
    float getY();

    boolean isSavable();

    MapkitItem getMapkitItem();

    void setWorld(World world);

    World getWorld();
}
