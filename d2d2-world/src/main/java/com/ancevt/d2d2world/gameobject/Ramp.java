package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.constant.Slowing;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

public class Ramp extends Sprite implements ICollision, IRotatable, IScalable, IRepeatable, ITight {

    private final MapkitItem mapkitItem;
    private final int gameObjectId;
    private boolean collisionEnabled;
    private float collisionX;
    private float collisionY;
    private float collisionWidth;
    private float collisionHeight;
    private World world;

    private int frameCounter;
    private int frameIndex;
    private float powerX;
    private float powerY;
    private boolean floorOnly;
    private String sound;

    public Ramp(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;

        setCollisionEnabled(true);

        setTexture(mapkitItem.getTexture());

        if (mapkitItem.getTextureCount(AnimationKey.IDLE) > 1) {
            addEventListener(Event.EACH_FRAME, e -> {
                frameCounter++;
                if (frameCounter >= Slowing.SLOWING / 2) {
                    frameCounter = 0;
                    frameIndex++;

                    if (frameIndex >= mapkitItem.getTextureCount(AnimationKey.IDLE)) {
                        frameIndex = 0;
                    }
                    setTexture(mapkitItem.getTexture(AnimationKey.IDLE, frameIndex));

                }
            });
        }
    }

    @Override
    public void onCollide(ICollision collideWith) {
        if (collideWith instanceof IGravitational g && g.getVelocityY() >= 0) {
            g.setVelocity(g.getVelocityX() + powerX, powerY);
            if(sound != null) playSound(sound);
        }
    }

    @Property
    public void setSound(String sound) {
        this.sound = sound;
    }

    @Property
    public String getSound() {
        return sound;
    }

    @Property
    public void setPowerX(float powerX) {
        this.powerX = powerX;
    }

    @Property
    public float getPowerX() {
        return powerX;
    }

    @Property
    public void setPowerY(float powerY) {
        this.powerY = powerY;
    }

    @Property
    public float getPowerY() {
        return powerY;
    }

    @Override
    public void process() {

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
    public int getGameObjectId() {
        return gameObjectId;
    }

    @Override
    public boolean isSavable() {
        return true;
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
    public float getOriginalWidth() {
        return getTexture().width();
    }

    @Override
    public float getOriginalHeight() {
        return getTexture().height();
    }

    @Override
    public void setFloorOnly(boolean b) {
        this.floorOnly = b;
    }

    @Override
    public boolean isFloorOnly() {
        return floorOnly;
    }

    @Override
    public void setPushable(boolean b) {

    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
