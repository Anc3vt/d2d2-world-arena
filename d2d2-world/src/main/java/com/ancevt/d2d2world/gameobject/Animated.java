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

import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.FramedSprite;
import com.ancevt.d2d2.display.IFramedDisplayObject;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.commons.unix.UnixDisplay.debug;
import static com.ancevt.d2d2world.D2D2World.isServer;

abstract public class Animated extends Container implements IAnimated, ISynchronized {

    private final MapkitItem mapkitItem;
    private final int gameObjectId;
    private int direction;
    private int currentAnimationKey = -1;
    private IFramedDisplayObject[] animations;
    private boolean backward;
    private boolean animationsPrepared;
    private boolean permanentSync;
    private World world;

    public Animated(@NotNull MapkitItem mapKitItem, int gameObjectId) {
        this.mapkitItem = mapKitItem;
        this.gameObjectId = gameObjectId;
        prepareAnimations();
        setPermanentSync(true);
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

    public void setBackward(boolean backward) {
        this.backward = backward;
    }

    public boolean isBackward() {
        return backward;
    }

    private void prepareAnimations() {
        animations = new IFramedDisplayObject[AnimationKey.MAX_ANIMATIONS];

        for (int animKey = 0; animKey < AnimationKey.MAX_ANIMATIONS; animKey++) {

            if (!getMapkitItem().isAnimationKeyExists(animKey)) continue;

            final int framesCount = getMapkitItem().getTextureCount(animKey);

            Texture[] frames = new Texture[framesCount];
            for (int i = 0; i < framesCount; i++) {
                frames[i] = getMapkitItem().getTexture(animKey, i);
            }

            IFramedDisplayObject framedDisplayObject = new FramedSprite(frames);
            framedDisplayObject.setFrame(0);
            framedDisplayObject.setLoop(true);
            framedDisplayObject.setSlowing(AnimationKey.SLOWING);
            animations[animKey] = framedDisplayObject;
            add(framedDisplayObject);
        }

        animationsPrepared = true;
        setAnimation(AnimationKey.IDLE);
    }

    @Override
    public void setVisible(boolean value) {
        super.setVisible(value);
        if (isOnWorld() && isPermanentSync()) {
            getWorld().getSyncDataAggregator().visibility(this);
        }
    }

    @Override
    public boolean isSavable() {
        return false;
    }

    @Override
    public int getAnimation() {
        return currentAnimationKey;
    }

    @Override
    public void setAnimation(final int animationKey) {
        //if (isServer()) { // <== TODO: please refactor and remove it

        if (!isServer() && this instanceof PlayerActor playerActor) {
            if(playerActor.isLocalPlayerActor()) setAnimation(animationKey, true);
        } else {
            setAnimation(animationKey, true);
        }

        //}
    }

    @Override
    public void setAnimation(int animationKey, boolean loop) {
        if (!animationsPrepared || animationKey == getAnimation()) return;

        if(this instanceof Actor actor && actor.getHook() != null) {
            //setAnimation(HOOK);
            return;
        }

        this.currentAnimationKey = animationKey;

        for (int i = 0; i < animations.length; i++) {
            IFramedDisplayObject currentFrameSet;
            IFramedDisplayObject fs = currentFrameSet = animations[i];

            if (getMapkitItem() == null) {
                debug("Animated:115: <A>" + this);
            }

            if (fs == null || !getMapkitItem().isAnimationKeyExists(animationKey)) continue;

            if (i != animationKey) currentFrameSet.setVisible(false);

            if (i == animationKey) {
                fs.setBackward(isBackward());
                fs.setLoop(loop);

                if (animationKey != AnimationKey.WALK_ATTACK) fs.setFrame(0);

                fs.play();
                fs.setVisible(true);
            }
        }

        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().animation(this, loop);
    }

    protected void fixXY() {
        for (IFramedDisplayObject fs : animations) {
            if (fs != null) {
                float leftOffset = direction == Direction.LEFT ? fs.getWidth() : 0;
                fs.setXY(-fs.getWidth() / 2 + leftOffset, -fs.getHeight() / 2);
            }
        }
    }

    @Override
    public int getGameObjectId() {
        return gameObjectId;
    }

    @Override
    public void setX(float value) {
        if (value == getX()) return;
        super.setX(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void setY(float value) {
        if (value == getY()) return;
        super.setY(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void setXY(float x, float y) {
        if (x == getX() && y == getY()) return;
        super.setX(x);
        super.setY(y);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void move(float toX, float toY) {
        if (toX == 0 && toY == 0) return;
        super.moveX(toX);
        super.moveY(toY);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void moveY(float value) {
        if (value == 0) return;
        super.moveY(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void moveX(float value) {
        if (value == 0) return;
        super.moveX(value);
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().xy(this);
    }

    @Override
    public void setDirection(int direction) {
        if (direction == this.direction) return;
        this.direction = direction;

        for (IFramedDisplayObject fs : animations) {
            if (fs == null) continue;

            if (direction == 0) {
                throw new IllegalStateException("direction is 0. Must be 1 or -1");
            }

            fs.setScaleX(direction);
        }

        fixXY();
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().direction(this);
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public void process() {

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
