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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.backend.norender.NoRenderBackend;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.constant.Slowing;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.map.Room;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.Camera;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

public class Parallax extends Sprite implements IGameObject, ISpeedable, IScalable, IRepeatable, IMovable {

    private final MapkitItem mapkitItem;
    private final int gameObjectId;
    private World world;
    private float speed;
    private int frameCounter;
    private int frameIndex;
    private Camera camera;
    private float startX;
    private float startY;
    private float offsetX;

    public Parallax(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;

        setTexture(mapkitItem.getTexture());

        if (mapkitItem.getTextureCount(AnimationKey.IDLE) > 1) {
            addEventListener(this, Event.ENTER_FRAME, this::this_eachFrame);
        }
    }

    @Override
    public void onAddToWorld(World world) {
        reset();
    }

    private void this_eachFrame(Event event) {
        frameCounter++;
        if (frameCounter >= Slowing.SLOWING) {
            frameCounter = 0;
            frameIndex++;

            if (frameIndex >= mapkitItem.getTextureCount(AnimationKey.IDLE)) {
                frameIndex = 0;
            }
            setTexture(mapkitItem.getTexture(AnimationKey.IDLE, frameIndex));
        }
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void process() {
        if (D2D2.getBackend() instanceof NoRenderBackend) return;

        final Room room = getWorld().getRoom();

        if(room != null) {
            final float roomWidth = room.getWidth();
            final float width = getWidth();
            setX(camera.getX() * speed + (roomWidth / width) - width / 3 + offsetX);
        }
    }

    @Property
    public void setOffsetX(float value) {
        offsetX = value;
    }

    @Property
    public float getOffsetX() {
        return offsetX;
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
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public float getSpeed() {
        return speed;
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
    public void setStartX(float x) {
        startX = x;
    }

    @Override
    public void setStartY(float y) {
        startY = y;
    }

    @Override
    public float getStartX() {
        return startX;
    }

    @Override
    public float getStartY() {
        return startY;
    }

    @Override
    public float getMovingSpeedX() {
        return 0;
    }

    @Override
    public float getMovingSpeedY() {
        return 0;
    }

    @Override
    public void setMovingSpeedX(float value) {

    }

    @Override
    public void setMovingSpeedY(float value) {

    }

    @Override
    public void reset() {
        setXY(getStartX(), getStartY());
    }
}
