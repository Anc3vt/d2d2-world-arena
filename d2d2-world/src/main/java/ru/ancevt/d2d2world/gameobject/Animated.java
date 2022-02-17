/*
 *   D2D2 World
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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
package ru.ancevt.d2d2world.gameobject;

import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.FramedSprite;
import ru.ancevt.d2d2.display.IFramedDisplayObject;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2world.constant.AnimationKey;
import ru.ancevt.d2d2world.constant.Direction;
import ru.ancevt.d2d2world.map.mapkit.MapkitItem;

abstract public class Animated extends DisplayObjectContainer implements IAnimated {

    private int direction;
    private int currentAnimationKey;
    private IFramedDisplayObject[] animations;
    private final MapkitItem mapkitItem;
    private final int id;


    public Animated(MapkitItem mapKitItem, int gameObjectId) {
        this.mapkitItem = mapKitItem;
        this.id = gameObjectId;

        prepareAnimations();
        setAnimation(AnimationKey.IDLE);
    }

    private void prepareAnimations() {
        animations = new IFramedDisplayObject[AnimationKey.MAX_ANIMATIONS];

        for (int animKey = 0; animKey < AnimationKey.MAX_ANIMATIONS; animKey++) {

            if (!mapkitItem.isAnimationKeyExists(animKey)) continue;

            final int framesCount = mapkitItem.getTextureCount(animKey);

            Texture[] frames = new Texture[framesCount];
            for (int i = 0; i < framesCount; i++) {
                frames[i] = mapkitItem.getTexture(animKey, i);
            }

            IFramedDisplayObject framedDisplayObject = new FramedSprite(frames);
            framedDisplayObject.setLoop(true);
            framedDisplayObject.setSlowing(AnimationKey.SLOWING);
            animations[animKey] = framedDisplayObject;
        }

        fixXY();
    }

    @Override
    public int getGameObjectId() {
        return id;
    }

    @Override
    public boolean isSavable() {
        return false;
    }

    @Override
    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }

    @Override
    public int getAnimation() {
        return currentAnimationKey;
    }

    @Override
    public void setAnimation(final int animationKey) {
        setAnimation(animationKey, true);
    }

    @Override
    public void setAnimation(final int animationKey, final boolean loop) {
        this.currentAnimationKey = animationKey;

        for (int i = 0; i < animations.length; i++) {
            IFramedDisplayObject currentFrameSet;
            IFramedDisplayObject fs = currentFrameSet = animations[i];

            if (fs == null || !getMapkitItem().isAnimationKeyExists(animationKey)) continue;

            if (i != animationKey) currentFrameSet.removeFromParent();

            if (i == animationKey) {
                fs.setLoop(loop);
                fs.play();
                add(fs);
            }

        }
    }

    private void fixXY() {
        for (IFramedDisplayObject fs : animations) {
            if(fs != null) {
                float leftOffset = direction == Direction.LEFT ? fs.getWidth() : 0;
                fs.setXY(-fs.getWidth() / 2 + leftOffset, -fs.getHeight() / 2);
            }
        }
    }


    @Override
    public void setDirection(int direction) {
        this.direction = direction;

        for (IFramedDisplayObject fs : animations) {
            if (fs == null) continue;

            if (direction == 0) {
                throw new IllegalStateException("direction is 0. Must be 1 or -1");
            }

            fs.setScaleX(direction);
        }

        fixXY();
    }

    @Override
    public int getDirection() {
        return direction;
    }

}
