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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.backend.norender.NoRenderStarter;
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
            addEventListener(Event.EACH_FRAME, Event.EACH_FRAME, this::this_eachFrame);
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
        if (D2D2.getStarter() instanceof NoRenderStarter) return;

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
