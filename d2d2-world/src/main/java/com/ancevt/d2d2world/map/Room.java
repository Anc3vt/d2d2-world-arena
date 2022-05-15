
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
    private int darkness;

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

    @Property
    public void setDarkness(int value) {
        darkness = value;
    }

    @Property
    public int getDarkness() {
        return darkness;
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
