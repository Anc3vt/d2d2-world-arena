/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    }

    @Override
    @Property
    public void setVertexBleedingFix(double v) {
        super.setVertexBleedingFix(v);
    }

    @Override
    @Property
    public double getVertexBleedingFix() {
        return super.getVertexBleedingFix();
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
