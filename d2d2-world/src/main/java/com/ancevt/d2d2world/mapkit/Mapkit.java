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

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.file.FileSystemUtils;
import com.ancevt.d2d2world.map.MapIO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class Mapkit {

    private final String name;
    // private final Map<String, MapkitItem> items;
    private final List<MapkitItem> items;
    private final Map<String, TextureAtlas> textureAtlases;

    Mapkit(String name) {
        this.items = new ArrayList<>();
        this.name = name;
        textureAtlases = new HashMap<>();
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
            InputStream inputStream = FileSystemUtils.getInputStream(MapIO.getMapkitsDirectory() + name + "/" + tilesetPngFilename);
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
        MapkitItem oldItem = getItemById(item.getId());
        if (oldItem != null) {
            throw new IllegalStateException("duplicate mapkit item id: " + item.getId() + ", " + item + ", old: " + oldItem);
        }
        items.add(item);
        return item;
    }

    public MapkitItem getItemById(String id) {
        for (MapkitItem item : items) {
            if (item.getId().equals(id)) return item;
        }
        return null; //TODO: refactor
    }

    public void removeItem(@NotNull MapkitItem item) {
        items.remove(item);
    }


    public List<MapkitItem> getItems() {
        return List.copyOf(items);
    }

    public void dispose() {
        textureAtlases.values().forEach(a -> D2D2.getTextureManager().unloadTextureAtlas(a));
    }

    @Override
    public String toString() {
        return "Mapkit{" +
                "name='" + name + '\'' +
                ", items=" + items.size() +
                '}';
    }

}
