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
package ru.ancevt.d2d2world.mapkit;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.texture.TextureAtlas;
import ru.ancevt.d2d2world.constant.ResourcePath;
import ru.ancevt.d2d2world.data.DataEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Mapkit {

    private final String name;
    private final Map<String, MapkitItem> items;
    private final String uid;
    private final Map<String, TextureAtlas> textureAtlases;

    Mapkit(String uid, String name) {
        this.uid = uid;
        this.items = new HashMap<>();
        this.name = name;
        textureAtlases = new HashMap<>();
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

    public TextureAtlas getTextureAtlas(String imageFileName) {
        if (textureAtlases.containsKey(imageFileName)) {
            return textureAtlases.get(imageFileName);
        }

        TextureAtlas textureAtlas = D2D2.getTextureManager().loadTextureAtlas(
                ResourcePath.MAPKITS + uid +'/' + imageFileName
        );

        textureAtlases.put(imageFileName, textureAtlas);
        return textureAtlas;
    }

    public MapkitItem createItem(DataEntry dataEntry) {
        return putItem(new MapkitItem(this, dataEntry));
    }

    public MapkitItem putItem(MapkitItem item) {
        if (items.containsKey(item.getId())) {
            throw new IllegalStateException("duplicate mapkit item id: " + item.getId());
        }
        items.put(item.getId(), item);
        return item;
    }

    public void removeItem(MapkitItem item) {
        items.remove(item.getId());
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
