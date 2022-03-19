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

import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.world.Layer;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.data.Properties.setProperties;

public class GameObjectUtils {

    public static IGameObject copy(IGameObject of, int newGameObjectId) {
        IGameObject result = of.getMapkitItem().createGameObject(newGameObjectId);
        return (IGameObject) setProperties(result, getProperties(of, DataEntry.newInstance()));
    }

    public static int getLayerIndex(IGameObject gameObject) {
        if (!gameObject.hasParent()) throw new IllegalStateException("Game object has no parent");

        return ((Layer) gameObject.getParent()).getIndex();
    }

}