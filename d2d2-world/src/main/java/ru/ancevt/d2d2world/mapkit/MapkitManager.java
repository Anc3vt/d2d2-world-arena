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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ancevt.d2d2world.data.DataKey;
import ru.ancevt.d2d2world.constant.ResourcePath;
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

    private static final String INDEX = "/index.mk";

    private final Map<String, Mapkit> mapkits;

    private MapkitManager() {
        mapkits = new HashMap<>();
        putBuiltInMapkits();
    }

    private void putBuiltInMapkits() {
        put(new CharacterMapkit());
        put(new AreaMapkit());
    }

    public Mapkit get(String uid) {
        if (!mapkits.containsKey(uid)) {
            throw new IllegalStateException("mapkit not found, uid: " + uid + ". Must be one of: " + mapkits.keySet());
        }
        return mapkits.get(uid);
    }

    public Mapkit load(String mapkitDirName) throws IOException {
        log.debug("load mapkit " + mapkitDirName);

        DataEntry[] dataLines = DataEntryLoader.load(
                ResourcePath.MAPKITS + mapkitDirName + INDEX
        );

        String uid = dataLines[0].getString(DataKey.UID);
        if (!uid.equals(mapkitDirName)) {
            throw new IllegalStateException("mapkit uid is different from directory name: " + uid + "," + mapkitDirName);
        }

        String name = dataLines[0].getString(DataKey.NAME);

        Mapkit mapkit = createMapkit(uid, name);

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

    public Mapkit getByName(String name) {
        for (Mapkit mapkit : mapkits.values()) {
            if(mapkit.getName().equals(name)) {
                return mapkit;
            }
        }
        throw new IllegalStateException("no suck mapkit name: " + name);
    }

    private void put(Mapkit mapkit) {
        mapkits.put(mapkit.getUid(), mapkit);
    }

    public void dispose(Mapkit mapkit) {
        if (mapkit instanceof CharacterMapkit || mapkit instanceof AreaMapkit) {
            throw new IllegalStateException("Unable to dispose built-in mapkit. name: " + mapkit.getName());
        }

        mapkits.remove(mapkit.getUid());
        mapkit.dispose();
    }

    @Override
    public String toString() {
        return "MapkitManager{" +
                "mapkits=" + mapkits +
                '}';
    }

    private Mapkit createMapkit(String uid, String name) {
        return new Mapkit(uid, name);
    }

    public Set<String> keySet() {
        return mapkits.keySet();
    }
}
