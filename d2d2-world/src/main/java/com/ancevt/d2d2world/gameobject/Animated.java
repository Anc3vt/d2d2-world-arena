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
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.IFramedDisplayObject;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.util.args.Args;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

abstract public class Animated extends Container implements IAnimated, ISynchronized {

    private final Map<String, IFramedDisplayObject> animations;

    private final int gameObjectId;

    private final MapkitItem mapkitItem;

    private final float width;

    private final float height;

    private IDisplayObject currentAnimation;

    private int direction;

    private String currentAnimationKey;

    public Animated(MapkitItem mapkitItem, int gameObjectId) {
        this.gameObjectId = gameObjectId;
        this.mapkitItem = mapkitItem;

        animations = new HashMap<>();

        width = mapkitItem.getDataEntry().getFloat("width");
        height = mapkitItem.getDataEntry().getFloat("height");

        prepareAnimations();
    }

    public void prepareAnimations() {
        DataEntry e = mapkitItem.getDataEntry();

        e.getKeyValues().stream()
                .filter(keyValue -> keyValue.key().startsWith("animation:"))
                .findAny()
                .ifPresent(keyValue -> addAnimation(keyValue.key()));

        if (animations.containsKey(Animated.IDLE)) {
            IFramedDisplayObject framedDisplayObject = animations.get(Animated.IDLE);
            framedDisplayObject.setVisible(true);
        }
    }

    private void addAnimation(String key) {
        Texture mapkitItemTexture = mapkitItem.getTexture();

        CoordsInfo.parse(mapkitItem.getDataEntry(), key, height).ifPresent(coordsInfo -> {
            Texture[] textures = new Texture[coordsInfo.frameCount];

            for (int i = 0; i < textures.length; i++) {
                textures[i] = mapkitItemTexture.getSubtexture(
                        0,
                        (int) coordsInfo.yOnTileset,
                        (int) width,
                        (int) height
                );
            }

            IFramedDisplayObject framed = new FramedSprite(textures);

            animations.put(key, framed);
            framed.setVisible(false);
            add(framed, -width / 2, -height / 2);
        });
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public String getAnimation() {
        return currentAnimationKey;
    }

    @Override
    public void setAnimation(String animationKey) {
        IDisplayObject framed = animations.get(animationKey);
        if (framed != null) {
            this.currentAnimationKey = animationKey;
            this.currentAnimation = framed;
            framed.setVisible(true);
        } else {
            throw new IllegalStateException("%s: no such animation key %s".formatted(getName(), animationKey));
        }
    }

    @Override
    public void setAnimation(String animationKey, boolean loop) {
        this.currentAnimationKey = animationKey;
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

        animations.forEach((k, v) -> {
            v.setScaleX(direction);
        });

        fixXY();
        if (isOnWorld() && isPermanentSync()) getWorld().getSyncDataAggregator().direction(this);
    }

    protected void fixXY() {
        animations.forEach((k, v) -> {
            if (v != null) {
                float leftOffset = direction == Direction.LEFT ? v.getWidth() : 0;
                v.setXY(-v.getWidth() / 2 + leftOffset, -v.getHeight() / 2);
            }
        });
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public void process() {

    }

    @Override
    public boolean isSavable() {
        return false;
    }

    private static class CoordsInfo {

        float yOnTileset;
        int frameCount;

        private CoordsInfo(float yOnTileset, int frameCount) {
            this.yOnTileset = yOnTileset;
            this.frameCount = frameCount;
        }

        static Optional<CoordsInfo> parse(DataEntry dataEntry, String key, float height) {
            if (dataEntry.containsKey(key)) return Optional.empty();

            String s = dataEntry.getString(key);
            Args args = Args.of("_idle, ','");
            float y = args.next(float.class) * height;
            int frameCount = args.next(int.class);

            return Optional.of(new CoordsInfo(y, frameCount));
        }
    }
}
























