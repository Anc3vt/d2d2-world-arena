package com.ancevt.d2d2world.gameobject.pickup;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.constant.SoundKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IResettable;
import com.ancevt.d2d2world.gameobject.ISynchronized;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;

public abstract class Pickup extends DisplayObjectContainer implements ICollision, IResettable, ISynchronized {

    private static final int DEFAULT_RESPAWN_MILLIS = 30000;
    private final Sprite bubbleSprite;
    private final DisplayObjectContainer container;
    private final Sprite image;

    private int counter = 0;
    private boolean ready;
    private String pickUpSound;
    private long respawnTimeMillis;
    private long pickUpTimeMillis;
    private World world;

    public Pickup(MapkitItem mapkitItem, int gameObjectId) {
        setMapkitItem(mapkitItem);
        setGameObjectId(gameObjectId);
        container = new DisplayObjectContainer();
        bubbleSprite = new Sprite(D2D2World.getPickupBubbleTexture());
        bubbleSprite.setAlpha(0.75f);
        image = new Sprite(mapkitItem.getTexture());
        container.add(image);
        image.setXY(-image.getWidth() / 2, -image.getHeight() / 2 + 1);
        container.add(bubbleSprite, -16, -16);
        container.setScale(0.01f, 0.01f);
        add(container);
        setCollision(-8f / 2f, -8f / 2f, 16f / 2f, 16f / 2f);
        setScale(0.5f, 0.5f);

        setRespawnTimeMillis(DEFAULT_RESPAWN_MILLIS);
    }

    @Override
    public void onAddToWorld(World world) {
        ICollision.super.onAddToWorld(world);
        this.world = world;
        world.addEventListener(WorldEvent.WORLD_PROCESS + getGameObjectId(), WorldEvent.WORLD_PROCESS, this::world_worldProcess);
        addEventListener(Event.REMOVE_FROM_STAGE + getGameObjectId(), Event.REMOVE_FROM_STAGE, this::this_removeFromStage);
    }

    private void this_removeFromStage(Event event) {
        removeEventListener(Event.REMOVE_FROM_STAGE + getName());
        world.removeEventListener(WorldEvent.WORLD_PROCESS + getGameObjectId());
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
    public void onCollide(ICollision collideWith) {
        if (!D2D2World.isServer()) return;

        if (collideWith instanceof PlayerActor playerActor && pickUpTimeMillis == 0) {
            if (onPlayerActorPickUpPickup(playerActor)) {
                playPickUpSound();
                setVisible(false);
                setCollisionEnabled(false);
                pickUpTimeMillis = System.currentTimeMillis();

                if(playerActor.isOnWorld()) {
                    playerActor.getWorld().getSyncDataAggregator().pickUp(playerActor, getGameObjectId());
                }
            }
        }
    }

    @Override
    public void reset() {
        setVisible(true);
        ready = false;
        counter = 0;
        container.setScale(0.01f, 0.01f);
        setCollisionEnabled(true);
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

    public void playPickUpSound() {
        if (getMapkitItem().getDataEntry().containsKey(SoundKey.PICK_UP)) {
            getMapkitItem().playSound(SoundKey.PICK_UP);
        } else {
            getMapkitItem().getMapkit().playSound("pickup.ogg");
        }
    }

    @Override
    public boolean isSavable() {
        return true;
    }

    @Override
    public void process() {

    }

    @Override
    public void onEachFrame() {
        if (!ready) {
            container.toScale(1.1f, 1.1f);
            if (container.getScaleX() >= 1) {
                container.setScale(1, 1);
                ready = true;
            }
        } else {
            counter++;
            final float factor = 0.0025f;
            if (counter < 20) {
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
                    image.setXY(-image.getWidth() / 1.5f, -image.getHeight() / 2);
                } else {
                    image.setXY(-image.getWidth() / 2, -image.getHeight() / 2);
                }
                container.setXY(0, 0);
            }
        }
    }
}


































