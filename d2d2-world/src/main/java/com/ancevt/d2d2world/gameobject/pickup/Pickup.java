package com.ancevt.d2d2world.gameobject.pickup;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.constant.SoundKey;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IResettable;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public abstract class Pickup extends DisplayObjectContainer implements ICollision, IResettable {

    private final Sprite bubbleSprite;
    private final DisplayObjectContainer container;
    private final Sprite image;

    private int counter = 0;
    private boolean ready;
    private String pickUpSound;

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
    }

    public abstract void onPlayerActorPickUpPickup(PlayerActor playerActor);

    public Sprite getImage() {
        return image;
    }

    @Override
    public void reset() {
        setVisible(true);
        ready = false;
        counter = 0;
        container.setScale(0.01f, 0.01f);
        setCollisionEnabled(true);
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

    @Override
    public void onCollide(ICollision collideWith) {
        if (collideWith instanceof PlayerActor playerActor) {
            playPickUpSound();
            setVisible(false);
            setCollisionEnabled(false);
            if (D2D2World.isServer()) {
                onPlayerActorPickUpPickup(playerActor);
            }
        }
    }

    @Property
    public void setPickUpSound(String filename) {
        this.pickUpSound = filename;
    }

    @Property
    public String getPickUpSound() {
        return pickUpSound;
    }

    private void playPickUpSound() {
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

                if(this instanceof WeaponPickup) {
                    image.setXY(-image.getWidth() / 1.5f, -image.getHeight() / 2);
                } else {
                    image.setXY(-image.getWidth() / 2, -image.getHeight() / 2);
                }
                container.setXY(0, 0);
            }
        }
    }



}



































