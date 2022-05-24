
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.constant.Slowing;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

public class Scenery extends Sprite implements IGameObject, IRepeatable, IRotatable, IScalable, IAlphable, IColored {

    private final int gameObjectId;
    private final MapkitItem mapkitItem;
    private int frameCounter;
    private int frameIndex;
    private World world;
    private boolean isStatic;

    public Scenery(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        this.mapkitItem = mapkitItem;
        this.gameObjectId = gameObjectId;

        setTexture(mapkitItem.getTexture());

        if (mapkitItem.getTextureCount(AnimationKey.IDLE) > 1) {
            addEventListener(Event.EACH_FRAME, e -> {
                frameCounter++;
                if (frameCounter >= Slowing.SLOWING) {
                    frameCounter = 0;
                    frameIndex++;

                    if (frameIndex >= mapkitItem.getTextureCount(AnimationKey.IDLE)) {
                        frameIndex = 0;
                    }
                    setTexture(mapkitItem.getTexture(AnimationKey.IDLE, frameIndex));
                }
            });
        } else {
            isStatic = true;
        }

        setVertexBleedingFix(0.005);
    }

//    @Override
//    @Property
//    public void setTextureBleedingFix(double v) {
//        super.setTextureBleedingFix(v);
//    }
//
//    @Override
//    @Property
//    public double getTextureBleedingFix() {
//        return super.getTextureBleedingFix();
//    }

    @Override
    public void process() {

    }

    @Override
    public int getGameObjectId() {
        return gameObjectId;
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
    public boolean isSavable() {
        return true;
    }

    @Override
    public float getWidth() {
        return getTexture().width() * getRepeatX();
    }

    @Override
    public float getHeight() {
        return getTexture().height() * getRepeatY();
    }

    @Override
    public float getOriginalWidth() {
        return getTexture().width();
    }

    @Override
    public float getOriginalHeight() {
        return getTexture().height();
    }

    @Property
    public void setStatic(boolean b) {
        isStatic = b;
    }


    @Property
    public boolean isStatic() {
        return mapkitItem.getTextureCount(AnimationKey.IDLE) == 1 && isStatic;
    }

    @Override
    public void setColorHex(String colorHex) {
        setColor(Color.of(Integer.parseInt(colorHex, 16)));
    }

    @Override
    public String getColorHex() {
        return getColor().toHexString();
    }

    @Override
    public String toString() {
        return "Scenery{" +
                "gameObjectId=" + gameObjectId +
                ", isStatic=" + isStatic +
                '}';
    }
}
