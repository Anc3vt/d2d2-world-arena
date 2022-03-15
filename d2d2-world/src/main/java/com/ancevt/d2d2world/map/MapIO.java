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
package com.ancevt.d2d2world.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.DataEntryLoader;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.mapkit.AreaMapkit;
import com.ancevt.d2d2world.mapkit.Mapkit;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.Layer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.data.Properties.setProperties;

public class MapIO {

    private static final Logger log = LoggerFactory.getLogger(MapIO.class);

    public static GameMap load(String mapFileName) throws IOException {
        log.debug("load map: {}", mapFileName);

        DataEntry[] dataEntries = DataEntryLoader.load(MapIO.mapsDirectory + mapFileName);

        log.debug("loaded <g>{}<> data entries", dataEntries.length);

        GameMap map = new GameMap();

        Room room = null;

        Map<String, String> mapkitNamesVsUids = new HashMap<>();
        mapkitNamesVsUids.put(AreaMapkit.NAME, AreaMapkit.UID);

        for (DataEntry dataEntry : dataEntries) {

            //log.debug("loaded data entry {}", dataEntry.toString());

            if (dataEntry.containsKey(DataKey.MAP)) {
                setProperties(map, dataEntry);

                String mapkitUids = dataEntry.getString(DataKey.MAPKIT_UIDS);
                String mapkitNames = dataEntry.getString(DataKey.MAPKIT_NAMES);
                String[] splitUids = mapkitUids.split(",");
                String[] splitNames = mapkitNames.split(",");
                if (splitUids.length != splitNames.length) {
                    throw new IllegalStateException("mapkit uids count and names count are differs");
                }
                for (int i = 0; i < splitNames.length; i++) {
                    mapkitNamesVsUids.put(splitNames[i], splitUids[i]);
                    MapkitManager.getInstance().load(splitUids[i]);
                }
                continue;
            }

            if (dataEntry.containsKey(DataKey.ROOM)) {
                room = new Room(dataEntry.getString(DataKey.NAME), map);

                setProperties(room, dataEntry);

                room.setBackgroundColor(new Color(Objects.requireNonNull(dataEntry.getString(DataKey.BACKGROUND_COLOR))));

                map.putRoom(room);
                continue;
            }

            int gameObjectId = dataEntry.getInt(DataKey.ID, 0);

            if (gameObjectId == 0) gameObjectId = map.getNextFreeGameObjectId();

            String mapkitItemId = dataEntry.getString(DataKey.ITEM);
            int layer = dataEntry.getInt(DataKey.LAYER);

            String mapkitUid = mapkitNamesVsUids.get(dataEntry.getString(DataKey.MAPKIT));
            Mapkit mapkit = MapkitManager.getInstance().get(mapkitUid);

            if (room == null) throw new IllegalStateException("room undefined");

            room.addGameObject(layer, (IGameObject) setProperties(mapkit.getItem(mapkitItemId).createGameObject(gameObjectId), dataEntry));
        }

        return map;
    }

    public static String toString(GameMap map) {
        StringBuilder stringBuilder = new StringBuilder();

        DataEntry mapDataEntry = DataEntry.newInstance();
        mapDataEntry.add(DataKey.MAP);

        getProperties(map, mapDataEntry);

        stringBuilder.append(mapDataEntry.stringify()).append('\n');

        Arrays.stream(map.getRooms()).forEach(room -> {
            stringBuilder.append('\n');
            DataEntry roomDataEntry = DataEntry.newInstance();
            roomDataEntry.add(DataKey.ROOM);

            getProperties(room, roomDataEntry);

            roomDataEntry.add(DataKey.BACKGROUND_COLOR, room.getBackgroundColor().toHexString());

            stringBuilder.append(roomDataEntry.stringify()).append('\n');

            for (int layer = 0; layer < Layer.LAYER_COUNT; layer++) {
                int gameObjectCount = room.getGameObjectsCount(layer);
                for (int i = 0; i < gameObjectCount; i++) {
                    IGameObject gameObject = room.getGameObject(layer, i);
                    DataEntry gameObjectDataEntry = DataEntry.newInstance();
                    gameObjectDataEntry.add(DataKey.ID, gameObject.getGameObjectId());

                    gameObjectDataEntry.add(DataKey.MAPKIT, gameObject.getMapkitItem().getMapkit().getName());

                    gameObjectDataEntry.add(DataKey.ITEM, gameObject.getMapkitItem().getId());
                    gameObjectDataEntry.add(DataKey.LAYER, String.valueOf(layer));

                    getProperties(gameObject, gameObjectDataEntry);

                    stringBuilder.append(gameObjectDataEntry.stringify()).append('\n');
                }
            }
        });

        return stringBuilder.toString();
    }

    public static String mapsDirectory;
    public static String mapFileName;
    public static String mapkitsDirectory;

    public static String save(GameMap map, String mapFileName) {
        try {
            log.debug("Saving map " + mapFileName);
            Path path = Path.of(mapsDirectory + mapFileName);

            String mapString = MapIO.toString(map);

            Files.writeString(
                    path,
                    mapString,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            log.debug("Saved map " + mapFileName);

            return path.toString();
        } catch (IOException e) {
            log.error("Save fault", e);
            throw new IllegalStateException(e);
        }

    }
}
