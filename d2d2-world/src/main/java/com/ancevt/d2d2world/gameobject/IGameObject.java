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

import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.world.World;

import java.util.HashMap;
import java.util.Map;

public interface IGameObject extends IDisplayObject, IProcessable {

    Map<IGameObject, Map<String, Object>> extraMap = new HashMap<>();
    Map<IGameObject, World> worldMap = new HashMap<>();
    Map<IGameObject, Integer> gameObjectIdMap = new HashMap<>();
    Map<IGameObject, MapkitItem> mapkitItemMap = new HashMap<>();

    default Map<String, Object> extra() {
        return extraMap.computeIfAbsent(this, k -> new HashMap<>());
    }

    default void setGameObjectId(int id) {
        gameObjectIdMap.put(this, id);
    }

    default int getGameObjectId() {
        return gameObjectIdMap.getOrDefault(this, 0);
    }

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

    default void setMapkitItem(MapkitItem mapkitItem) {
        mapkitItemMap.put(this, mapkitItem);
    }

    default MapkitItem getMapkitItem() {
        return mapkitItemMap.get(this);
    }

    default void setWorld(World world) {
        worldMap.putIfAbsent(this, world);
    }

    default World getWorld() {
        return worldMap.get(this);
    }

    default boolean isOnWorld() {
        return getWorld() != null;
    }

    default void onAddToWorld(World world) {
        setWorld(world);
    }
}
