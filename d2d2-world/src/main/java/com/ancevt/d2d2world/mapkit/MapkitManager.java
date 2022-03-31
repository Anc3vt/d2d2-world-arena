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

import com.ancevt.commons.Holder;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.DataEntryLoader;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.map.MapIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
        put(new BuiltInMapkit());
        put(new AreaMapkit());
    }

    public void disposeExternalMapkits() {
        Queue<String> keysToRemove = new LinkedList<>();
        mapkits.values()
                .stream()
                .filter(m -> m instanceof ExternalMapkit)
                .forEach(m -> {
                    m.dispose();
                    keysToRemove.add(m.getName());
                });

        while (!keysToRemove.isEmpty()) {
            mapkits.remove(keysToRemove.poll());
        }
    }

    public Mapkit load(String mapkitName) throws IOException {
        log.debug("load mapkit '{}'", mapkitName);

        String dirName = getMapkitDirNameByMapkitName(mapkitName);

        DataEntry[] dataLines = DataEntryLoader.load(MapIO.mapkitsDirectory + dirName + INDEX);

        String name = dataLines[0].getString(DataKey.NAME);

        Mapkit mapkit = createExternalMapkit(name);

        for (DataEntry dataEntry : dataLines) {
            //log.debug("loaded data line: " + dataEntry.toString());

            if (dataEntry.containsKey(DataKey.MAPKIT)) {
                continue;
            }

            mapkit.createItem(dataEntry);
        }

        put(mapkit);

        return mapkit;
    }

    public static String getMapkitDirNameByMapkitName(String mapkitName) {

        Holder<String> dirName = new Holder<>();
        try {

//            File mapkitsDir = new File(MapIO.mapkitsDirectory);
//            File[] mapkitDirs = mapkitsDir.listFiles();
//            for (File mapkitDir : mapkitDirs) {
//                if(new File(indexPath).exists()) {
//            }


            Files.walk(Path.of(MapIO.mapkitsDirectory), 1)
                    .forEach(path -> {
                        String indexPath = path.toFile().getAbsolutePath() + INDEX;

                        if(new File(indexPath).exists()) {
                            DataEntry[] dataEntries = DataEntryLoader.load(indexPath);
                            for (DataEntry dataEntry : dataEntries) {
                                if (dataEntry.containsKey(DataKey.MAPKIT)) {
                                    String name = dataEntry.getString(DataKey.NAME);
                                    if (mapkitName.equals(name)) {
                                        dirName.setValue(path.getFileName().toString());
                                        return;
                                    }
                                }
                            }
                        }
                    });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return dirName.getValue();
    }

    public Mapkit getMapkit(String mapkitName) {
        for (Mapkit mapkit : mapkits.values()) {
            if (mapkit.getName().equals(mapkitName)) {
                return mapkit;
            }
        }
        throw new IllegalStateException("no such mapkit name: " + mapkitName);
    }

    private void put(Mapkit mapkit) {
        mapkits.put(mapkit.getName(), mapkit);
    }

    public void dispose(Mapkit mapkit) {
        if (mapkit instanceof BuiltInMapkit || mapkit instanceof AreaMapkit) {
            throw new IllegalStateException("Unable to dispose built-in mapkit. name: " + mapkit.getName());
        }

        mapkits.remove(mapkit.getName());
        mapkit.dispose();
    }

    @Override
    public String toString() {
        return "MapkitManager{" +
                "mapkits=" + mapkits +
                '}';
    }

    private Mapkit createExternalMapkit(String name) {
        return new ExternalMapkit(name);
    }

    public Set<String> keySet() {
        return mapkits.keySet();
    }
}
