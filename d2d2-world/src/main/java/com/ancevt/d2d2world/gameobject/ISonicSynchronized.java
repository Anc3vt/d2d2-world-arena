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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.sound.D2D2WorldSound;

public interface ISonicSynchronized extends IGameObject {

    default void playSound(String soundFilenameFromMapkit) {
        if (isOnWorld()) {
            String mapkitName = getMapkitItem().getMapkit().getName();
            String path = MapIO.getMapkitsDirectory() + mapkitName + "/" + soundFilenameFromMapkit;
            D2D2WorldSound.playSoundDoubleSafe(path, getWorld().getCamera(), getX(), getY());
            getWorld().getSyncDataAggregator().sound(this, soundFilenameFromMapkit);
        }
    }

    default void playSoundFromServer(String soundFilenameFromMapkit) {
        if (isOnWorld()) {
            String mapkitName = getMapkitItem().getMapkit().getName();
            String path = MapIO.getMapkitsDirectory() + mapkitName + "/" + soundFilenameFromMapkit;
            D2D2WorldSound.playSoundDoubleSafe(path, getWorld().getCamera(), getX(), getY());
        }
    }
}
