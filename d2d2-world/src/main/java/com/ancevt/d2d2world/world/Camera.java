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
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.display.IContainer;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.gameobject.IDirectioned;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.PlayerActor_;

import static java.lang.Math.abs;

public class Camera {

    public static final float DEFAULT_VIEWPORT_WIDTH = D2D2World.ORIGIN_WIDTH;
    public static final float DEFAULT_VIEWPORT_HEIGHT = D2D2World.ORIGIN_HEIGHT;
    public static float DEFAULT_ZOOM = 1.0f;

    private static final float MIN_ZOOM = 0.2f;
    private static final float MAX_ZOOM = 3.0f;

    private final World world;
    private boolean boundsLock;

    private float viewportWidth;
    private float viewportHeight;

    private float boundWidth;
    private float boundHeight;
    private IGameObject attachedTo;
    private int direction;

    public Camera(World world) {
        this.world = world;
        setViewportSize(DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT);
    }

    public void setViewportSize(float viewportWidth, float viewportHeight) {
        setViewportWidth(viewportWidth);
        setViewportHeight(viewportHeight);
    }

    public void setViewportWidth(float viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    public void setViewportHeight(float viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public float getViewportWidth() {
        return viewportWidth;
    }

    public float getViewportHeight() {
        return viewportHeight;
    }

    public float getZoom() {
        return worldParent().getScaleX();
    }

    public void zoom(float delta) {
        setZoom(getZoom() * delta);
    }

    public void setZoom(float zoom) {
        if (zoom < 1.1f) zoom = 1.0f;

        worldParent().setScale(zoom, zoom);
        if (worldParent().getScaleX() < MIN_ZOOM)
            worldParent().setScale(MIN_ZOOM, MIN_ZOOM);
        else if (worldParent().getScaleX() > MAX_ZOOM)
            worldParent().setScale(MAX_ZOOM, MAX_ZOOM);

        if (isBoundsLock()) fixBounds();
    }

    public float getX() {
        return -world.getX();
    }

    public float getY() {
        return -world.getY();
    }

    public final void setXY(float x, float y) {
        world.setXY(-x, -y);
    }

    public final void setX(float x) {
        world.setX(-x);
    }

    public final void setY(float y) {
        world.setY(-y);
    }

    public final void move(float toX, float toY) {
        world.move(-toX / getZoom(), -toY / getZoom());
        if (isBoundsLock()) fixBounds();
    }

    public final void moveX(float value) {
        world.moveX(-value / getZoom());
        if (isBoundsLock()) fixBounds();
    }

    public final void moveY(float value) {
        world.moveY(-value / getZoom());
        if (isBoundsLock()) fixBounds();
    }

    public final IContainer worldParent() {
        return world.getParent();
    }

    public boolean isBoundsLock() {
        return boundsLock;
    }

    public void setBoundsLock(boolean boundsLock) {
        this.boundsLock = boundsLock;
        if (boundsLock) fixBounds();
    }

    private void fixBounds() {
        if (boundWidth == 0 || boundHeight == 0) return;

        float halfViewportWidth = viewportWidth / 2;
        float halfViewportHeight = viewportHeight / 2;

        float z = getZoom();

        float minLimitX = halfViewportWidth / z;
        float minLimitY = halfViewportHeight / z;
        float maxLimitX = boundWidth - halfViewportWidth / z;
        float maxLimitY = boundHeight - halfViewportHeight / z;

        if (getX() < minLimitX) setX(minLimitX);
        else if (getX() > maxLimitX) setX(maxLimitX);
        if (getY() < minLimitY) setY(minLimitY);
        else if (getY() > maxLimitY) setY(maxLimitY);

        if (boundWidth < viewportWidth / z) setX(boundWidth / 2);
        if (boundHeight < viewportHeight / z) setY(boundHeight / 2);

    }

    public float getBoundWidth() {
        return boundWidth;
    }

    public float getBoundHeight() {
        return boundHeight;
    }

    public void setBounds(float w, float h) {
        boundWidth = w;
        boundHeight = h;
        if (isBoundsLock()) fixBounds();
    }

    public IGameObject getAttachedTo() {
        return attachedTo;
    }

    public void setAttachedTo(IGameObject attachedTo) {
        this.attachedTo = attachedTo;
    }

    public final void process() {
        processAttached();
    }

    private void processAttached() {
        if (attachedTo == null) return;

        if (attachedTo instanceof IDirectioned d) {
            setDirection(d.getDirection());
        }

        boolean left = getDirection() == Direction.LEFT;

        float smooth = 25f;
        float side = 80.0f;

        float aX = attachedTo.getX();
        float aY = attachedTo.getY();
        float x = getX() + (left ? side : -side);
        float y = getY();

        float factorX = attachedTo instanceof PlayerActor_ playerActor && abs(playerActor.getVelocityX()) > 6 ? 7.5f : 2f;
        float factorY = attachedTo instanceof PlayerActor_ playerActor && abs(playerActor.getVelocityY()) > 5 ? 7.5f : 3f;

        if (aX > x) {
            float t = (aX - x) / (smooth / factorX);
            moveX(t);
        } else if (aX < x) {
            float t = (x - aX) / (smooth / factorX);
            moveX(-t);
        }

        if (aY > y) {
            float t = (aY - y) / (smooth / factorY);
            moveY(t);
        } else if (aY < y) {
            float t = (y - aY) / (smooth / factorY);
            moveY(-t);
        }
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

}
