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
package com.ancevt.d2d2world.mapkit;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.data.IntRectangle;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.area.Area;
import com.ancevt.d2d2world.gameobject.pickup.Pickup;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

import static com.ancevt.d2d2world.data.Properties.setProperties;

@Slf4j
public class MapkitItem {

    private static final int MAX_TEXTURE_TYPES = 16;

    private final Mapkit mapkit;
    private final DataEntry dataEntry;

    private final Texture[][] textures;

    public MapkitItem(Mapkit mapkit, DataEntry dataEntry) {
        this.mapkit = mapkit;
        this.dataEntry = dataEntry;

        textures = prepareTextures();
    }

    public Mapkit getMapkit() {
        return mapkit;
    }

    public Class<? extends IGameObject> getGameObjectClass() {
        String classname = dataEntry.getString(DataKey.CLASS);
        if (!classname.contains(".")) {
            if (classname.contains("Area")) {
                classname = Area.class.getPackageName() + "." + classname;
            } else if (classname.contains("Pickup")) {
                classname = Pickup.class.getPackageName() + "." + classname;
            } else {
                classname = IGameObject.class.getPackageName() + "." + classname;
            }
        }

        try {
            return (Class<? extends IGameObject>) Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public @NotNull IGameObject createGameObject(int gameObjectId) {
        try {
            var gameObject = getGameObjectClass()
                    .getDeclaredConstructor(MapkitItem.class, int.class)
                    .newInstance(this, gameObjectId);
            setProperties(gameObject, dataEntry);
            return (IGameObject) gameObject;
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(dataEntry + " ---" + this, e);
        }
    }

    public String getId() {
        return dataEntry.getString(DataKey.ID);
    }

    public final DataEntry getDataEntry() {
        return dataEntry;
    }

    public Texture getTexture() {
        return textures[AnimationKey.IDLE][0];
    }

    public TextureAtlas getTextureAtlas() {
        return getTexture().getTextureAtlas();
    }

    public Sprite getIcon() {
        return new Sprite(getTexture());
    }

    public final Texture getTexture(int animationKey, int frameIndex) {
        return textures[animationKey][frameIndex];
    }

    public final Texture getTexture(String dataKey) {
        return getTextureAtlas().createTexture(getDataEntry().getString(dataKey));
    }

    public final Texture[] getTextures(String dataKey) {
        return getTextureAtlas().createTextures(getDataEntry().getString(dataKey));
    }

    public final int getTextureCount(final int animationKey) {
        return textures[animationKey].length;
    }

    public final boolean isAnimationKeyExists(final int animationKey) {
        return textures[animationKey] != null;
    }

    private Texture[] @NotNull [] prepareTextures() {
        final Texture[][] result = new Texture[MAX_TEXTURE_TYPES][];

        result[AnimationKey.IDLE] = prepareTexturesOfKey(DataKey.IDLE);
        result[AnimationKey.WALK] = prepareTexturesOfKey(DataKey.WALK);
        result[AnimationKey.ATTACK] = prepareTexturesOfKey(DataKey.ATTACK);
        result[AnimationKey.JUMP] = prepareTexturesOfKey(DataKey.JUMP);
        result[AnimationKey.JUMP_ATTACK] = prepareTexturesOfKey(DataKey.JUMP_ATTACK);
        result[AnimationKey.WALK_ATTACK] = prepareTexturesOfKey(DataKey.WALK_ATTACK);
        result[AnimationKey.DAMAGE] = prepareTexturesOfKey(DataKey.DAMAGE);
        result[AnimationKey.DEFENSE] = prepareTexturesOfKey(DataKey.DEFENSE);
        result[AnimationKey.HOOK] = prepareTexturesOfKey(DataKey.HOOK);
        result[AnimationKey.HOOK_ATTACK] = prepareTexturesOfKey(DataKey.HOOK_ATTACK);
        result[AnimationKey.FALL] = prepareTexturesOfKey(DataKey.FALL);
        result[AnimationKey.FALL_ATTACK] = prepareTexturesOfKey(DataKey.FALL_ATTACK);
        result[AnimationKey.EXTRA_ANIMATION] = prepareTexturesOfKey(DataKey.EXTRA_ANIMATION);
        result[AnimationKey.DEATH] = prepareTexturesOfKey(DataKey.DEATH);

        return result;
    }

    private Texture @Nullable [] prepareTexturesOfKey(final String key) {

        if (!dataEntry.containsKey(key)) return null;

        final IntRectangle[] tilesetZones = IntRectangle.getIntRectangles(dataEntry.getString(key));

        final Texture[] result = new Texture[tilesetZones.length];

        for (int i = 0; i < tilesetZones.length; i++) {
            final IntRectangle tz = tilesetZones[i];

            final TextureAtlas atlas = mapkit.getTextureAtlas(dataEntry.getString(DataKey.ATLAS));
            if (atlas != null) {
                result[i] = atlas.createTexture(
                        tz.getX(), tz.getY(), tz.getWidth(), tz.getHeight()
                );
            }
        }

        return result;
    }
}
