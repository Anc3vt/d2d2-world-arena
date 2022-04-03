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
package com.ancevt.d2d2world.map;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.world.Layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room {

    private final List<IGameObject>[] gameObjects;

    private String id;
    private final GameMap map;
    private int width;
    private int height;
    private Color backgroundColor;

    public Room(String name, GameMap map) {
        this.id = name;
        this.map = map;

        gameObjects = new List[Layer.LAYER_COUNT];

        for (int i = 0; i < gameObjects.length; i++) {
            gameObjects[i] = new ArrayList<>();
        }
    }

    @Property
    public String getId() {
        return id;
    }

    @Property
    public void setId(String newId) {
        this.id = newId;
    }

    public GameMap getMap() {
        return map;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public IGameObject[] getGameObjects() {
        List<IGameObject> result = new ArrayList<>();
        for (List<IGameObject> gameObject : gameObjects) {
            result.addAll(gameObject);
        }
        return result.toArray(new IGameObject[0]);
    }

    @Property
    public void setWidth(int width) {
        this.width = width;
    }

    @Property
    public int getWidth() {
        return width;
    }

    @Property
    public void setHeight(int height) {
        this.height = height;
    }

    @Property
    public int getHeight() {
        return height;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public final void addGameObject(int layer, IGameObject gameObject) {
        gameObjects[layer].add(gameObject);
    }

    public final void removeGameObject(int layer, IGameObject gameObject) {
        gameObjects[layer].remove(gameObject);
    }

    public final int getGameObjectsCount(int layer) {
        return gameObjects[layer].size();
    }

    public final IGameObject getGameObject(int layer, int index) {
        return gameObjects[layer].get(index);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", gameObjects=" + Arrays.toString(gameObjects) +
                ", width=" + width +
                ", height=" + height +
                ", backgroundColor=" + backgroundColor +
                '}';
    }

    public int getLayerIndexOfGameObject(IGameObject gameObject) {
        for(int i = 0; i < gameObjects.length; i ++) {
            List<IGameObject> list = gameObjects[i];
            if(list.contains(gameObject)) return i;
        }

        throw new IllegalStateException("Game object " + gameObject.getName() + " has no layer");
    }
}
