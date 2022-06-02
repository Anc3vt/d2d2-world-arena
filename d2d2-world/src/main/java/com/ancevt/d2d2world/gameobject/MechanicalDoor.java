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

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import org.jetbrains.annotations.NotNull;

public class MechanicalDoor extends Sprite implements
        ISynchronized,
        ISonicSynchronized,
        ITight,
        IResettable {

    private static final float OPEN_SPEED = 4;

    private int timeTacts;
    private boolean opening;
    private boolean closing;
    private float closedCollisionWidth;
    private float closedCollisionHeight;

    private int tact;

    public MechanicalDoor(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem.getTexture());
        setMapkitItem(mapkitItem);
        setGameObjectId(gameObjectId);
        setCollisionEnabled(true);
    }

    @Override
    public void onCollide(ICollision collideWith) {
        if (collideWith instanceof Actor /* || collideWith instanceof Weapon.Bullet */) {
            if (getCollisionHeight() > 0) open();
        }
    }

    public void open() {
        closing = false;
        opening = true;
        if (getMapkitItem().getDataEntry().containsKey(DataKey.OPEN_SOUND)) {
            playSound(getMapkitItem().getDataEntry().getString(DataKey.OPEN_SOUND));
        }
    }

    public void close() {
        closing = true;
        setAlpha(1f);
        if (getMapkitItem().getDataEntry().containsKey(DataKey.CLOSE_SOUND)) {
            playSound(getMapkitItem().getDataEntry().getString(DataKey.CLOSE_SOUND));
        }
    }

    @Override
    public void process() {
        if (opening) {
            setCollisionHeight(getCollisionHeight() - OPEN_SPEED);
            if (getCollisionHeight() < 0) setCollisionHeight(0);
            setTexture(getMapkitItem().getTexture().getSubtexture(0, 0, (int) getCollisionWidth(), (int) getCollisionHeight()));

            if (getCollisionHeight() <= 0) {
                opening = false;
                tact = getTimeTacts();
                setAlpha(0f);
            }
        }

        if (closing) {
            setCollisionHeight(getCollisionHeight() + OPEN_SPEED);
            setTexture(getMapkitItem().getTexture().getSubtexture(0, 0, (int) getCollisionWidth(), (int) getCollisionHeight()));

            if (getCollisionHeight() >= closedCollisionHeight) {
                closing = false;
            }
        }

        if (tact > 0) {
            tact--;

            if (tact == 0) {
                close();
            }
        }
    }

    @Property
    public int getTimeTacts() {
        return timeTacts;
    }

    @Property
    public void setTimeTacts(int timeTacts) {
        this.timeTacts = timeTacts;
    }

    @Property
    public void setClosedCollisionWidth(float closedCollisionWidth) {
        this.closedCollisionWidth = closedCollisionWidth;
    }

    @Property
    public float getClosedCollisionWidth() {
        return closedCollisionWidth;
    }

    @Property
    public void setClosedCollisionHeight(float closedCollisionHeight) {
        this.closedCollisionHeight = closedCollisionHeight;
    }

    @Property
    public float getClosedCollisionHeight() {
        return closedCollisionHeight;
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public void reset() {
        if (isOnWorld()) getWorld().getSyncDataAggregator().reset(this);
        setCollisionWidth(closedCollisionWidth);
        setCollisionHeight(closedCollisionHeight);
        setTexture(getMapkitItem().getTexture());
        tact = 0;
        opening = false;
    }
}
