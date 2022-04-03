/*
 *   D2D2 World Editor
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
package com.ancevt.d2d2world.editor;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.mapkit.AreaMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class Cursor extends Sprite {

    private MapkitItem mapkitItem;

    public Cursor() {
        setAlpha(0.5f);
    }

    public void setMapKitItem(MapkitItem mapkitItem) {
        this.mapkitItem = mapkitItem;

        if (mapkitItem != null) {
            setTexture(mapkitItem.getIcon().getTexture());

            if (mapkitItem.getMapkit() instanceof AreaMapkit) {
                setScale(10f, 10f);
                setColor(mapkitItem.getIcon().getColor());
            } else {
                setScale(1f, 1f);
                setColor(Color.WHITE);
            }

            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }
}
