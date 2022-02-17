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

import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.EventListener;
import ru.ancevt.d2d2world.constant.AnimationKey;
import ru.ancevt.d2d2world.constant.Slowing;
import ru.ancevt.d2d2world.map.mapkit.MapkitItem;
import ru.ancevt.d2d2world.world.World;

public class Scenery extends Sprite implements IGameObject, IRepeatable, IRotatable, IScalable, IAlphable, EventListener {

    private final int gameObjectId;
    private final MapkitItem mapkitItem;
    private int frameCounter;
    private int frameIndex;
    private World world;

    public Scenery(MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;

        setTexture(mapkitItem.getTexture());

        if (mapkitItem.getTextureCount(AnimationKey.IDLE) > 1) {
            addEventListener(Event.EACH_FRAME, this);
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void process() {

    }

    @Override
    public int getGameObjectId() {
        return gameObjectId;
    }

    @Override
    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    public boolean isPackable() {
        return mapkitItem.getTextureCount(AnimationKey.IDLE) == 1;
    }

    @Override
    public float getWidth() {
        return getTexture().width() * getRepeatX();
    }

    @Override
    public float getHeight() {
        return getTexture().height() * getRepeatY();
    }

    @Override
    public float getOriginalWidth() {
        return getTexture().width();
    }

    @Override
    public float getOriginalHeight() {
        return getTexture().height();
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType().equals(Event.EACH_FRAME)) {
            frameCounter++;
            if (frameCounter >= Slowing.SLOWING) {
                frameCounter = 0;
                frameIndex++;

                if(frameIndex >= mapkitItem.getTextureCount(AnimationKey.IDLE)) {
                    frameIndex = 0;
                }
                setTexture(mapkitItem.getTexture(AnimationKey.IDLE, frameIndex));
            }
        }
    }
}
