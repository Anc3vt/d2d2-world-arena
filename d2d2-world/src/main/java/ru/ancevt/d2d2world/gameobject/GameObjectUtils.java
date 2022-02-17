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

import ru.ancevt.d2d2world.data.DataEntry;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.map.mapkit.MapkitItem;
import ru.ancevt.d2d2world.world.Layer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static ru.ancevt.d2d2world.data.Properties.getProperties;
import static ru.ancevt.d2d2world.data.Properties.setProperties;

public class GameObjectUtils {

    public static IGameObject copy(IGameObject of, int newGameObjectId) {
        try {
            Constructor<?> constructor = of.getClass().getDeclaredConstructor(MapkitItem.class, int.class);
            IGameObject result = (IGameObject) constructor.newInstance(of.getMapkitItem(), newGameObjectId);

            return (IGameObject) setProperties(result, getProperties(of, DataEntry.newInstance()));
        } catch (NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public static int getLayerIndex(IGameObject gameObject) {
        if (!gameObject.hasParent()) throw new IllegalStateException("Game object has no parent");

        return ((Layer) gameObject.getParent()).getIndex();
    }

}