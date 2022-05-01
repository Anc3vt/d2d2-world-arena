package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;

public class SceneryRect extends PlainRect implements ISizable, IColored, IAlphable, IRotatable {

    private final MapkitItem mapkitItem;
    private final int gameObjectId;
    private World world;

    public SceneryRect(MapkitItem mapkitItem, int gameObjectId) {
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
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void process() {

    }

    @Override
    public void setColorHex(String colorHex) {
        setColor(Color.of(Integer.parseInt(colorHex, 16)));
    }

    @Override
    public String getColorHex() {
        return getColor().toHexString();
    }
}
