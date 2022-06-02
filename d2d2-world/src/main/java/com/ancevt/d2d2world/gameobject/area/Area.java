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

package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.ISizable;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;

abstract public class Area extends BorderedRect implements IGameObject, ICollision, ISizable {

    private static final float DEFAULT_WIDTH = 16.0f;
    private static final float DEFAULT_HEIGHT = 16.0f;
    private static final float ALPHA = 0.25f;

    private final int gameObjectId;

    protected BitmapText bitmapText;

    private boolean collisionEnabled;
    private final MapkitItem mapkitItem;
    private float collisionWidth;
    private float collisionHeight;
    private float collisionX;
    private float collisionY;
    private World world;

    protected Area(MapkitItem mapkitItem, int gameObjectId) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;
        this.collisionEnabled = true;

        bitmapText = new BitmapText();

        setAlpha(ALPHA);
        setTextVisible(true);
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
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setCollision(0, 0, width, height);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        setCollisionWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        setCollisionHeight(height);
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
    public void process() {
        // To override
    }

    @Override
    public void setFillColor(Color color) {
        super.setFillColor(color);
    }

    public final void setText(final Object o) {
        bitmapText.setText(String.valueOf(o));
    }

    public final String getText() {
        return bitmapText.getText();
    }

    public final void setTextVisible(boolean value) {
        if (value && !isTextVisible()) {
            remove(bitmapText);
        } else if (!value && isTextVisible()) {
            add(bitmapText);
        }
    }

    public final boolean isTextVisible() {
        return bitmapText.hasParent();
    }


    @Override
    public void setCollisionEnabled(boolean value) {
        this.collisionEnabled = value;
    }

    @Override
    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    @Override
    public void setCollisionWidth(float collisionWidth) {
        this.collisionWidth = collisionWidth;
    }

    @Override
    public float getCollisionWidth() {
        return collisionWidth;
    }

    @Override
    public void setCollisionHeight(float collisionHeight) {
        this.collisionHeight = collisionHeight;
    }

    @Override
    public float getCollisionHeight() {
        return collisionHeight;
    }

    @Override
    public void setCollisionX(float collisionX) {
        this.collisionX = collisionX;
    }

    @Override
    public float getCollisionX() {
        return collisionX;
    }

    @Override
    public void setCollisionY(float collisionY) {
        this.collisionY = collisionY;
    }

    @Override
    public float getCollisionY() {
        return collisionY;
    }

}
