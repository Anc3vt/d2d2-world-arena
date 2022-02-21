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
package ru.ancevt.d2d2world.gameobject.area;

import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2world.gameobject.ITight;
import ru.ancevt.d2d2world.mapkit.MapkitItem;

public class AreaCollision extends Area implements ITight {

    public static final Color FILL_COLOR = Color.BLACK;
    private static final Color FILL_FLOOR_ONLY_COLOR = Color.WHITE;
    private static final Color STROKE_COLOR = Color.WHITE;

    private boolean floorOnly;

    private boolean pushable;

    public AreaCollision(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setFillColor(FILL_COLOR);
        setBorderColor(STROKE_COLOR);
    }

    @Override
    public void setFloorOnly(boolean b) {
        floorOnly = b;
        setFillColor(floorOnly ? FILL_FLOOR_ONLY_COLOR : FILL_COLOR);
    }

    @Override
    public boolean isFloorOnly() {
        return floorOnly;
    }

    @Override
    public void setPushable(boolean pushable) {
        this.pushable = pushable;
    }

    @Override
    public boolean isPushable() {
        return pushable;
    }

}
