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
package ru.ancevt.d2d2world.gameobject.area;

import ru.ancevt.d2d2.common.BorderedRect;
import ru.ancevt.d2d2.display.text.BitmapText;
import ru.ancevt.d2d2world.gameobject.ICollision;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.gameobject.ISizable;
import ru.ancevt.d2d2world.map.mapkit.MapkitItem;
import ru.ancevt.d2d2world.world.World;

public abstract class Area extends BorderedRect implements IGameObject, ICollision, ISizable {

    private static final float DEFAULT_WIDTH = 16.0f;
    private static final float DEFAULT_HEIGHT = 16.0f;
    private static final float ALPHA = 0.25f;

    protected BitmapText bitmapText;

    private final int id;

    private boolean collisionEnabled;
    private final MapkitItem mapkitItem;
    private World world;

    protected Area(MapkitItem mapkitItem, int gameObjectId) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.id = gameObjectId;
        this.collisionEnabled = true;

        bitmapText = new BitmapText();

        setAlpha(ALPHA);
        setTextVisible(true);

        this.mapkitItem = mapkitItem;
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
