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
package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.ISizable;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;

public abstract class Area extends BorderedRect implements IGameObject, ICollision, ISizable {

    private static final float DEFAULT_WIDTH = 16.0f;
    private static final float DEFAULT_HEIGHT = 16.0f;
    private static final float ALPHA = 0.25f;

    protected BitmapText bitmapText;

    private final int id;

    private boolean collisionEnabled;
    private final MapkitItem mapkitItem;
    private Sprite pointSprite;
    private World world;

    protected Area(MapkitItem mapkitItem, int gameObjectId) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.id = gameObjectId;
        this.collisionEnabled = true;

        bitmapText = new BitmapText();

        setAlpha(ALPHA);
        setTextVisible(true);

        this.mapkitItem = mapkitItem;

        //setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
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
    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }

    @Override
    public int getGameObjectId() {
        return id;
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public void setCollisionEnabled(boolean value) {
        collisionEnabled = value;
    }

    @Override
    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    @Override
    public void setCollisionX(float collisionX) {
        // Not used by design
    }

    @Override
    public void setCollisionY(float collisionY) {
        // Not used by design
    }

    @Override
    public float getCollisionX() {
        return 0;
    }

    @Override
    public float getCollisionY() {
        return 0;
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        checkAndSetPoint();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        checkAndSetPoint();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        checkAndSetPoint();
    }

    private void checkAndSetPoint() {
        if(pointSprite == null) {
            pointSprite = new Sprite("d2d2-world-common-tileset-area-point");
            pointSprite.setXY(-pointSprite.getWidth() / 2, -pointSprite.getHeight() / 2);
        }

        if (getWidth() <= 17 && getHeight() <= 17) {
            add(pointSprite);
        } else {
            pointSprite.removeFromParent();
        }
    }

    @Override
    public void setCollisionWidth(float collisionWidth) {
        setWidth(collisionWidth);
    }

    @Override
    public float getCollisionWidth() {
        return getWidth();
    }

    @Override
    public void setCollisionHeight(float collisionHeight) {
        setHeight(collisionHeight);
    }

    @Override
    public float getCollisionHeight() {
        return getHeight();
    }

    @Override
    public void setCollision(float x, float y, float width, float height) {
        setCollisionX(x);
        setCollisionY(y);
        setCollisionWidth(width);
        setCollisionHeight(height);
    }

    @Override
    public void onCollide(ICollision collideWith) {
        // To override
    }

    @Override
    public void process() {
        // To override
    }

    @Override
    public void setFillColor(Color color) {
        if(pointSprite != null) pointSprite.setColor(color);
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

}
