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
package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.IDamaging;
import com.ancevt.d2d2world.mapkit.MapkitItem;

        public class AreaDamaging extends Area implements IDamaging {
    public static final Color FILL_COLOR = Color.DARK_RED;
    private static final Color STROKE_COLOR = Color.RED;

    public static final int DEFAULT_DAMAGING_POWER = 20;

    private int damagingPower;

    public AreaDamaging(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setBorderColor(STROKE_COLOR);
        setFillColor(FILL_COLOR);
        setDamagingPower(DEFAULT_DAMAGING_POWER);

        bitmapText.setColor(Color.RED);
    }

    @Override
    public final void setDamagingPower(final int damagingPower) {
        this.damagingPower = damagingPower;
        setText(String.valueOf(damagingPower));
    }

    @Override
    public final int getDamagingPower() {
        return this.damagingPower;
    }

    @Override
    public void setDamagingOwnerActor(Actor actor) {

    }

    @Override
    public Actor getDamagingOwnerActor() {
        return null;
    }


}
