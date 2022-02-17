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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2world.constant.DataKey;
import ru.ancevt.d2d2world.data.DataEntry;
import ru.ancevt.d2d2world.data.DataEntryLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapkitManager {

    private static final MapkitManager INSTANCE = new MapkitManager();

    public static MapkitManager getInstance() {
        return INSTANCE;
    }

    private static final Logger log = LoggerFactory.getLogger(MapkitManager.class);

    private static final String MAPKIT_DIR = "mapkit/";
    private static final String INDEX = "/mapkit.dat";
    private static final String TILESET = "/tileset.png";

    private final Map<String, Mapkit> mapkits;

    private MapkitManager() {
        mapkits = new HashMap<>();
        putBuiltInMapkits();
    }

    private void putBuiltInMapkits() {
        put(new PlayerMapkit());
        put(new AreaMapkit());
    }

    public Mapkit get(String id) {
        if (!mapkits.containsKey(id)) {
            throw new IllegalStateException("mapkit not found, id: " + id + ". Must be one of: " + mapkits.keySet());
        }
        return mapkits.get(id);
    }

    public Mapkit load(String mapkitDirName) throws IOException {
        log.debug("load " + mapkitDirName);

        DataEntry[] dataLines = DataEntryLoader.load(
                MAPKIT_DIR + mapkitDirName + INDEX
        );

        Mapkit mapkit = createMapkit(dataLines[0].getString(DataKey.ID));

        mapkit.setTextureAtlas(D2D2.getTextureManager().loadTextureAtlas(MAPKIT_DIR + mapkitDirName + TILESET));

        for (DataEntry dataEntry : dataLines) {
            log.debug("loaded data line: " + dataEntry.toString());

            if (dataEntry.containsKey(DataKey.MAPKIT)) {
                continue;
            }

            mapkit.createItem(dataEntry);
        }

        put(mapkit);

        return mapkit;
    }

    private void put(Mapkit mapkit) {
        mapkits.put(mapkit.getId(), mapkit);
    }

    public void dispose(Mapkit mapkit) {
        if (mapkit instanceof PlayerMapkit || mapkit instanceof AreaMapkit) {
            throw new IllegalStateException("Unable to dispose built-in mapkit. Id: " + mapkit.getId());
        }

        mapkits.remove(mapkit.getId());
        D2D2.getTextureManager().unloadTextureAtlas(mapkit.getTextureAtlas());
    }

    @Override
    public String toString() {
        return "MapkitManager{" +
                "mapkits=" + mapkits +
                '}';
    }

    private Mapkit createMapkit(String name) {
        return new Mapkit(name);
    }

    public Set<String> keySet() {
        return mapkits.keySet();
    }
}
