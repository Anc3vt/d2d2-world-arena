package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import org.jetbrains.annotations.NotNull;

public class Pickup extends DisplayObjectContainer implements ICollision {

    private final MapkitItem mapkitItem;
    private final int gameObjectId;

    public Pickup(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;
    }

    @Override
    public int getGameObjectId() {
        return gameObjectId;
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }

    @Override
    public void process() {

    }
}
