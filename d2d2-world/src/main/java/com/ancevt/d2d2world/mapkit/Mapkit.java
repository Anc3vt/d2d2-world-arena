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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.sound.Sound;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.map.MapIO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class Mapkit {

    private final String name;
    private final Map<String, MapkitItem> items;
    private final String uid;
    private final Map<String, TextureAtlas> textureAtlases;
    private final Map<String, Sound> sounds;

    Mapkit(String uid, String name) {
        this.uid = uid;
        this.items = new HashMap<>();
        this.name = name;
        textureAtlases = new HashMap<>();
        sounds = new HashMap<>();
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public final int getItemCount() {
        return items.size();
    }

    public TextureAtlas getTextureAtlas(String tilesetPngFilename) {
        if (textureAtlases.containsKey(tilesetPngFilename)) {
            return textureAtlases.get(tilesetPngFilename);
        }

        try {
            InputStream inputStream = new FileInputStream(MapIO.mapkitsDirectory + uid + "/" + tilesetPngFilename);
            TextureAtlas textureAtlas = D2D2.getTextureManager().loadTextureAtlas(inputStream);
            inputStream.close();

            textureAtlases.put(tilesetPngFilename, textureAtlas);
            return textureAtlas;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public MapkitItem createItem(DataEntry dataEntry) {
        return putItem(new MapkitItem(this, dataEntry));
    }

    public MapkitItem putItem(@NotNull MapkitItem item) {
        if (items.containsKey(item.getId())) {
            throw new IllegalStateException("duplicate mapkit item id: " + item.getId());
        }
        items.put(item.getId(), item);
        return item;
    }

    public void removeItem(@NotNull MapkitItem item) {
        items.remove(item.getId());
    }

    @SneakyThrows
    public void playSound(String filename) {
        Sound.play(MapIO.mapkitsDirectory + uid + "/" + filename);
    }

    public MapkitItem getItem(String mapkitItemId) {
        MapkitItem mapkitItem = items.get(mapkitItemId);

        if (mapkitItem == null) throw new IllegalStateException("Mapkit item not defined, id: " + mapkitItemId
                + ". Mapkit: " + getName());
        return mapkitItem;
    }

    public Set<String> keySet() {
        return items.keySet();
    }

    public void dispose() {
        textureAtlases.values().forEach(a -> D2D2.getTextureManager().unloadTextureAtlas(a));
    }

    @Override
    public String toString() {
        return "Mapkit{" +
                "name='" + name + '\'' +
                ", items=" + items.size() +
                ", uid='" + uid + '\'' +
                '}';
    }

}
