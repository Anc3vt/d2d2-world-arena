/*
 *   D2D2 World
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2world.mapkit;

import com.ancevt.d2d2world.gameobject.area.*;
import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.data.DataEntry;

public class AreaMapkit extends Mapkit {

    public static final String NAME = "areas";

    AreaMapkit() {
        super(NAME);
        addItems();
    }

    private void addItems() {
        putItem(new AreaMapkitItem(this, "collision", AreaCollision.class, AreaCollision.FILL_COLOR));
        putItem(new AreaMapkitItem(this, "checkpoint", AreaCheckpoint.class, AreaCheckpoint.FILL_COLOR));
        putItem(new AreaMapkitItem(this, "damaging", AreaDamaging.class, AreaDamaging.FILL_COLOR));
        putItem(new AreaMapkitItem(this, "door-teleport", AreaDoorTeleport.class, AreaDoorTeleport.FILL_COLOR));
        putItem(new AreaMapkitItem(this, "hook", AreaHook.class, AreaHook.FILL_COLOR));
        putItem(new AreaMapkitItem(this, "target", AreaTarget.class, AreaTarget.FILL_COLOR));
    }

    private static class AreaMapkitItem extends MapkitItem {

        private final Sprite icon;

        public AreaMapkitItem(Mapkit mapkit, String name, Class<?> gameObjectClass, Color color) {
            super(mapkit, createDataEntry(name, gameObjectClass));
            icon = new PlainRect(1.0f, 1.0f, color);
        }

        private static @NotNull DataEntry createDataEntry(String id, @NotNull Class<?> gameObjectClass) {
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
