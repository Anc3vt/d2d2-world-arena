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
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.ISizable;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public abstract class Area extends BorderedRect implements IGameObject, ICollision, ISizable {

    private static final float DEFAULT_WIDTH = 16.0f;
    private static final float DEFAULT_HEIGHT = 16.0f;
    private static final float ALPHA = 0.25f;

    protected BitmapText bitmapText;

    private boolean collisionEnabled;

    protected Area(MapkitItem mapkitItem, int gameObjectId) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMapkitItem(mapkitItem);
        setGameObjectId(gameObjectId);

        this.collisionEnabled = true;

        bitmapText = new BitmapText();

        setAlpha(ALPHA);
        setTextVisible(true);
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



}
