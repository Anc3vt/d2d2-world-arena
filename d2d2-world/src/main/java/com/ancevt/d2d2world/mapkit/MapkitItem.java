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
package com.ancevt.d2d2world.mapkit;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.sound.Sound;
import com.ancevt.d2d2world.constant.AnimationKey;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.constant.SoundKey;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.IntRectangle;
import com.ancevt.d2d2world.gameobject.IGameObject;

import java.lang.reflect.InvocationTargetException;

public class MapkitItem {

    private static final int MAX_TEXTURE_TYPES = 16;

    private final Mapkit mapkit;
    private final DataEntry dataEntry;

    private final Texture[][] textures;
    private final Sound[][] sounds;



    private Class<?> gameObjectClass;

    public MapkitItem(Mapkit mapkit, DataEntry dataEntry) {
        this.mapkit = mapkit;
        this.dataEntry = dataEntry;

        textures = prepareTextures();
        sounds = prepareSounds();
    }

    public Mapkit getMapkit() {
        return mapkit;
    }

    public Class<?> getGameObjectClass() {
        try {
            return Class.forName(dataEntry.getString(DataKey.CLASS));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public IGameObject createGameObject(int gameObjectId) {
        try {
            return (IGameObject)
                    getGameObjectClass().getDeclaredConstructor(MapkitItem.class, int.class).newInstance(
                            this,
                            gameObjectId
                    );

        } catch (InstantiationException |
                NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e + " " + this.dataEntry, e);
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

    public Sprite getIcon() {
        return new Sprite(getTexture());
    }

    public final Texture getTexture(int animationKey, int frameIndex) {
        return textures[animationKey][frameIndex];
    }

    public final int getTextureCount(final int animationKey) {
        return textures[animationKey].length;
    }

    public final boolean isAnimationKeyExists(final int animationKey) {
        return textures[animationKey] != null;
    }

    private Texture[][] prepareTextures() {
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

        return result;
    }

    private Texture[] prepareTexturesOfKey(final String key) {

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


    private Sound[][] prepareSounds() {
        final Sound[][] result = new Sound[SoundKey.MAX_SOUNDS][];

        result[SoundKey.JUMP] = prepareSoundsOfKey(DataKey.SOUND_JUMP);
        result[SoundKey.DAMAGE] = prepareSoundsOfKey(DataKey.SOUND_DAMAGE);
        result[SoundKey.EXTRA_SOUND] = prepareSoundsOfKey(DataKey.SOUND_EXTRA);
        result[SoundKey.DEATH] = prepareSoundsOfKey(DataKey.SOUND_DEATH);

        return result;
    }

    private Sound[] prepareSoundsOfKey(final String key) {
//        final String value = dataLine.getString(key);
//
//        final String[] valueStrings = value.split(";");
//
//        final Sound[] result = new Sound[valueStrings.length];
//
//        for (int i = 0; i < valueStrings.length; i++) {
//            final String current = valueStrings[i];
//
//            final String path = Path.MAPKIT_DIRECTORY + mapkit.getDirectory() + current;
//
//            final Sound sound = new Sound(path);
//            result[i] = sound;
//        }
//
//        return result;
        return null;
    }

    public final void playSound(final int soundKey) {
        playSound(soundKey, 0);
    }

    public final void playSound(final int soundKey, final int index) {
        if (sounds[soundKey] == null || sounds[soundKey].length <= index || sounds[soundKey][index] == null) {
            return;
        }

        sounds[soundKey][index].play();
    }
}
