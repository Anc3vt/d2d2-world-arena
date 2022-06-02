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

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.IDamaging;
import com.ancevt.d2d2world.gameobject.ITight;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class AreaDamaging extends Area implements IDamaging, ITight {
    public static final Color FILL_COLOR = Color.DARK_RED;
    private static final Color STROKE_COLOR = Color.RED;

    public static final int DEFAULT_DAMAGING_POWER = 20;

    private int damagingPower;
    private Actor damagingOwnerActor;
    private boolean floorOnly;
    private boolean pushable;

    public AreaDamaging(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setBorderColor(STROKE_COLOR);
        setFillColor(FILL_COLOR);
        setDamagingPower(DEFAULT_DAMAGING_POWER);

        setPushable(false);
        setFloorOnly(false);

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
        this.damagingOwnerActor = actor;
    }

    @Override
    public Actor getDamagingOwnerActor() {
        return damagingOwnerActor;
    }


    @Override
    public void setFloorOnly(boolean b) {
        floorOnly = b;
    }

    @Override
    public boolean isFloorOnly() {
        return floorOnly;
    }

    @Override
    public void setPushable(boolean b) {
        pushable = b;
    }

    @Override
    public boolean isPushable() {
        return pushable;
    }
}
