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

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.constant.Slowing;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

public class Scenery extends Sprite implements IGameObject, IRepeatable, IRotatable, IScalable, IAlphable, IColored {

    private final int gameObjectId;
    private final MapkitItem mapkitItem;
    private int frameCounter;
    private int frameIndex;
    private World world;
    private boolean isStatic;

    public Scenery(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;

        setTexture(mapkitItem.getTexture());

        if (mapkitItem.getTextureCount(AnimationKey.IDLE) > 1) {
            addEventListener(Event.EACH_FRAME, e -> {
                frameCounter++;
                if (frameCounter >= Slowing.SLOWING) {
                    frameCounter = 0;
                    frameIndex++;

                    if (frameIndex >= mapkitItem.getTextureCount(AnimationKey.IDLE)) {
                        frameIndex = 0;
                    }
                    setTexture(mapkitItem.getTexture(AnimationKey.IDLE, frameIndex));
                }
            });
        } else {
            isStatic = true;
        }

        setVertexBleedingFix(0.5);
    }

    @Override
    @Property
    public void setBleedingFix(double v) {
        super.setBleedingFix(v);
    }

    @Override
    @Property
    public double getBleedingFix() {
        return super.getBleedingFix();
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
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean isSavable() {
        return true;
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

    @Property
    public void setStatic(boolean b) {
        isStatic = b;
    }


    @Property
    public boolean isStatic() {
        return mapkitItem.getTextureCount(AnimationKey.IDLE) == 1 && isStatic;
    }

    @Override
    public void setColorHex(String colorHex) {
        setColor(Color.of(Integer.parseInt(colorHex, 16)));
    }

    @Override
    public String getColorHex() {
        return getColor().toHexString();
    }

    @Override
    public String toString() {
        return "Scenery{" +
                "gameObjectId=" + gameObjectId +
                ", isStatic=" + isStatic +
                '}';
    }
}
