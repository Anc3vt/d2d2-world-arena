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
package ru.ancevt.d2d2world.map.mapkit;

import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2world.constant.DataKey;
import ru.ancevt.d2d2world.data.DataEntry;
import ru.ancevt.d2d2world.gameobject.area.AreaCheckpoint;
import ru.ancevt.d2d2world.gameobject.area.AreaCollision;
import ru.ancevt.d2d2world.gameobject.area.AreaDamaging;
import ru.ancevt.d2d2world.gameobject.area.AreaDoorTeleport;
import ru.ancevt.d2d2world.gameobject.area.AreaHook;

public class AreaMapkit extends Mapkit {

    private static final String ID = "areas";

    AreaMapkit() {
        super(ID);
        addItems();
    }

    private void addItems() {
        putItem(new AreaMapkitItem(this, "collision", AreaCollision.class, AreaCollision.FILL_COLOR));
        putItem(new AreaMapkitItem(this, "checkpoint", AreaCheckpoint.class, AreaCheckpoint.FILL_COLOR));
        putItem(new AreaMapkitItem(this, "damaging", AreaDamaging.class, AreaDamaging.FILL_COLOR));
        putItem(new AreaMapkitItem(this, "door-teleport", AreaDoorTeleport.class, AreaDoorTeleport.FILL_COLOR));
        putItem(new AreaMapkitItem(this, "hook", AreaHook.class, AreaHook.FILL_COLOR));
    }

    private static class AreaMapkitItem extends MapkitItem {

        private final Sprite icon;

        public AreaMapkitItem(Mapkit mapkit, String id, Class<?> gameObjectClass, Color color) {
            super(mapkit, createDataEntry(id, gameObjectClass));

            icon = new PlainRect(1.0f, 1.0f, color);
        }

        private static DataEntry createDataEntry(String id, Class<?> gameObjectClass) {
            DataEntry dataEntry = DataEntry.newInstance();
            dataEntry.add(DataKey.ID, id);
            dataEntry.add(DataKey.CLASS, gameObjectClass.getName());
            return dataEntry;
        }

        @Override
        public Sprite getIcon() {
            return icon;
        }
    }


}