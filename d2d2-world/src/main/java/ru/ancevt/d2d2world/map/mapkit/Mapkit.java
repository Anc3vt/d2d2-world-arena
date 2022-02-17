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
package ru.ancevt.d2d2world.map.mapkit;

import ru.ancevt.d2d2.display.texture.TextureAtlas;
import ru.ancevt.d2d2world.data.DataEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Mapkit {

    private final String id;
    private final Map<String, MapkitItem> items;
    private TextureAtlas textureAtlas;

    Mapkit(String id) {
        this.items = new HashMap<>();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTextureAtlas(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    public final TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public final int getItemCount() {
        return items.size();
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
                + ". Mapkit: " + getId());
        return mapkitItem;
    }

    public Set<String> keySet() {
        return items.keySet();
    }

    @Override
    public String toString() {
        return "Mapkit{" +
                "name='" + id + '\'' +
                ", items=" + items +
                ", textureAtlas=" + textureAtlas +
                '}';
    }
}
