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
package com.ancevt.d2d2world.gameobject.pickup;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.D2D2WorldAssets;
import com.ancevt.d2d2world.constant.SoundKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IResettable;
import com.ancevt.d2d2world.gameobject.ISonicSynchronized;
import com.ancevt.d2d2world.gameobject.ISynchronized;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2world.D2D2World.isServer;

abstract public class Pickup extends Container implements ICollision, IResettable, ISynchronized, ISonicSynchronized {

    public static final int PICKUP_DISAPPEAR_AFTER_TACT = 5000;

    private static final int DEFAULT_RESPAWN_MILLIS = 30000;

    private final MapkitItem mapkitItem;
    private final int gameObjectId;
    protected final Sprite image;
    protected final Container container;
    protected final Sprite bubbleSprite;

    private int counter = 0;
    private int tact = 0;
    private boolean ready;
    private String pickUpSound;
    private long respawnTimeMillis;
    private long pickUpTimeMillis;
    private int disappearAfterTact;
    private World world;
    private float collisionY;
    private float collisionX;
    private float collisionHeight;
    private float collisionWidth;
    private boolean collisionEnabled;
    private boolean permanentSync;
    private boolean outStarted;

    public Pickup(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;
        container = new Container();
        bubbleSprite = new Sprite(D2D2WorldAssets.getPickupBubbleTexture32());
        bubbleSprite.setAlpha(0.75f);
        image = new Sprite(mapkitItem.getTexture());
        container.add(image);
        image.setXY(-image.getWidth() / 2, -image.getHeight() / 2);
        container.add(bubbleSprite, -16, -16);
        container.setScale(0.01f, 0.01f);
        add(container);
        setCollision(-8f / 2f, -8f / 2f, 16f / 2f, 16f / 2f);
        setRespawnTimeMillis(DEFAULT_RESPAWN_MILLIS);
        setCollisionEnabled(true);
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
    public void onAddToWorld(World world) {
        // TODO: fix bug of double call this method
        ICollision.super.onAddToWorld(world);
        this.world = world;
        world.removeEventListener(this, WorldEvent.WORLD_PROCESS);
        world.addEventListener(this, WorldEvent.WORLD_PROCESS, this::world_worldProcess);
        removeEventListener(this, Event.REMOVE_FROM_STAGE);
        addEventListener(this, Event.REMOVE_FROM_STAGE, this::this_removeFromStage);
    }

    private void this_removeFromStage(Event event) {
        removeEventListener(this, Event.REMOVE_FROM_STAGE);
        world.removeEventListener(this, WorldEvent.WORLD_PROCESS);
    }

    private void world_worldProcess(Event event) {
        if (!isVisible() && pickUpTimeMillis > 0 && respawnTimeMillis > 0) {

            long now = System.currentTimeMillis();

            if (now - pickUpTimeMillis > respawnTimeMillis) {
                reset();
                pickUpTimeMillis = 0;
            }
        }
    }

    public abstract boolean onPlayerActorPickUpPickup(PlayerActor playerActor);

    public Sprite getImage() {
        return image;
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

    @Override
    public void onCollide(ICollision collideWith) {
        if (!isServer()) return;

        if (collideWith instanceof PlayerActor playerActor && pickUpTimeMillis == 0 && playerActor.isAlive()) {
            var result = onPlayerActorPickUpPickup(playerActor);

            if (result) {
                setVisible(false);
                setCollisionEnabled(false);
                pickUpTimeMillis = System.currentTimeMillis();

                if (respawnTimeMillis <= 0) {
                    world.removeEventListener(this, WorldEvent.WORLD_PROCESS);
                    // delayed actually removing pickup game object from serverside world
                    Async.runLater(1, TimeUnit.SECONDS, () -> world.removeGameObject(this, false));
                }

                if (playerActor.isOnWorld()) {
                    playerActor.getWorld().getSyncDataAggregator().pickUp(playerActor, getGameObjectId());
                }
            }
        }
    }

    @Override
    public void reset() {
        setVisible(true);
        setCollisionEnabled(true);
        ready = false;
        counter = 0;
        container.setScale(0.01f, 0.01f);
        pickUpTimeMillis = 0;

        if (isOnWorld()) getWorld().getSyncDataAggregator().reset(this);
    }

    @Override
    public void setVisible(boolean value) {
        super.setVisible(value);
        if (isOnWorld()) getWorld().getSyncDataAggregator().visibility(this);
    }

    @Override
    public float getWidth() {
        return getCollisionWidth();
    }

    @Override
    public float getHeight() {
        return getCollisionHeight();
    }

    public void setBubbleColor(Color color) {
        bubbleSprite.setColor(color);
    }

    public Color getBubbleColor() {
        return bubbleSprite.getColor();
    }

    @Property
    public void setRespawnTimeMillis(long respawnTimeMillis) {
        this.respawnTimeMillis = respawnTimeMillis;
    }

    @Property
    public long getRespawnTimeMillis() {
        return respawnTimeMillis;
    }

    @Property
    public void setPickUpSound(String filename) {
        this.pickUpSound = filename;
    }

    @Property
    public String getPickUpSound() {
        return pickUpSound;
    }

    public void setDisappearAfterTact(int disappearAfterTact) {
        this.disappearAfterTact = disappearAfterTact;
    }

    public int getDisappearAfterTact() {
        return disappearAfterTact;
    }

    public void playPickUpSound() {
        if (getMapkitItem().getDataEntry().containsKey(SoundKey.PICK_UP)) {
            playSound(getMapkitItem().getDataEntry().getString(SoundKey.PICK_UP));
        } else {
            playSound("pickup.ogg");
        }
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
        tact++;

        if (!outStarted && disappearAfterTact != 0 && tact > disappearAfterTact) {
            startOut();
            if (isOnWorld()) {
                getWorld().getSyncDataAggregator().pickupDisappear(this);
            }
        }
    }

    public void startOut() {
        outStarted = true;
        addEventListener(Event.ENTER_FRAME, event -> {
            toScale(0.8f, 0.8f);
            if (getScaleX() <= 0.05f) {
                getWorld().removeGameObject(this, false);
            }
        });
    }

    @Override
    public void onEachFrame() {
        if (!ready) {
            container.toScale(1.25f, 1.25f);
            if (container.getScaleX() >= 1) {
                container.setScale(1, 1);
                ready = true;
            }
        } else {
            counter++;
            final float factor = 0.0025f;
            if (counter <= 20) {
                container.setScale(container.getScaleX() + factor, container.getScaleY() - factor);
                image.moveY(-0.01f);
                container.moveY(0.25f);
            } else {
                container.setScale(container.getScaleX() - factor, container.getScaleY() + factor);
                image.moveY(+0.01f);
                container.moveY(-0.25f);
            }

            if (counter > 40) {
                counter = 0;
                container.setScale(1, 1);
                image.setScale(1, 1);

                if (this instanceof WeaponPickup) {
                    image.setXY(-image.getWidth() / 3, -image.getHeight() / 2);
                } else {
                    image.setXY(-image.getWidth() / 2, -image.getHeight() / 2);
                }
                container.setXY(0, 0);
            }
        }
    }

    @Override
    public void setPermanentSync(boolean permanentSync) {
        this.permanentSync = permanentSync;
    }

    @Override
    public boolean isPermanentSync() {
        return permanentSync;
    }
}



































