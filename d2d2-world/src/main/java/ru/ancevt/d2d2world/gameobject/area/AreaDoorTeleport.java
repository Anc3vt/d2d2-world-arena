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
import ru.ancevt.d2d2world.map.mapkit.MapkitItem;

public class AreaDoorTeleport extends Area {

    public static final Color FILL_COLOR = Color.BLUE;
    private static final Color STROKE_COLOR = Color.WHITE;

    private String targetRoomId;

    private int targetX;

    private int targetY;

    public AreaDoorTeleport(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setFillColor(FILL_COLOR);
        setBorderColor(STROKE_COLOR);
    }


    public final void setTargetLocation(final int x, final int y) {
        setTargetX(x);
        setTargetY(y);
    }

    public void setTargetX(int value) {
        this.targetX = value;
    }

    public int getTargetX() {
        return targetX;
    }

    public void setTargetY(int value) {
        this.targetY = value;
    }

    public int getTargetY() {
        return targetY;
    }

    public void setTargetRoomId(String targetRoomId) {
        this.targetRoomId = targetRoomId;
    }

    public String getTargetRoomId() {
        return targetRoomId;
    }



}
