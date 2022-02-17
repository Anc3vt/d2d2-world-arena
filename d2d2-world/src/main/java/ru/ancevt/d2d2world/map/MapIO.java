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
package ru.ancevt.d2d2world.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2world.constant.DataKey;
import ru.ancevt.d2d2world.data.DataEntry;
import ru.ancevt.d2d2world.data.DataEntryLoader;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.map.mapkit.Mapkit;
import ru.ancevt.d2d2world.map.mapkit.MapkitManager;
import ru.ancevt.d2d2world.world.Layer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static ru.ancevt.d2d2world.data.Properties.getProperties;
import static ru.ancevt.d2d2world.data.Properties.setProperties;

public class MapIO {

    private static final Logger log = LoggerFactory.getLogger(MapIO.class);

    private static final String MAP_DIR = "map/";

    public static GameMap load(String mapFileName) throws IOException {
        log.debug("load map: {}", mapFileName);

        DataEntry[] dataEntries = DataEntryLoader.load(MAP_DIR + mapFileName);

        log.debug("loaded {} data entries", dataEntries.length);

        GameMap map = new GameMap();

        Room room = null;

        for (DataEntry dataEntry : dataEntries) {

            log.debug("loaded data entry {}", dataEntry.toString());

            if (dataEntry.containsKey(DataKey.MAP)) {
                setProperties(map, dataEntry);

                map.setMapkit(MapkitManager.getInstance().load(map.getUsingMapkit()));
                continue;
            }

            if (dataEntry.containsKey(DataKey.ROOM)) {
                room = new Room(dataEntry.getString(DataKey.ID), map);

                setProperties(room, dataEntry);

                room.setBackgroundColor(new Color(Objects.requireNonNull(dataEntry.getString(DataKey.BACKGROUND_COLOR))));

                map.putRoom(room);
                continue;
            }

            int gameObjectId = dataEntry.getInt(DataKey.ID, 0);

            if (gameObjectId == 0) gameObjectId = map.getNextFreeGameObjectId();

            String mapkitItemId = dataEntry.getString(DataKey.ITEM);
            int layer = dataEntry.getInt(DataKey.LAYER);

            Mapkit mapkit = dataEntry.containsKey(DataKey.MAPKIT) ?
                    MapkitManager.getInstance().get(dataEntry.getString(DataKey.MAPKIT)) : map.getMapkit();

            if (room == null) throw new IllegalStateException("room undefined");

            room.addGameObject(
                    layer,
                    (IGameObject) setProperties(mapkit.getItem(mapkitItemId).createGameObject(gameObjectId), dataEntry)
            );
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

                    if (gameObject.getMapkitItem().getMapkit() != map.getMapkit()) {
                        gameObjectDataEntry.add(DataKey.MAPKIT, gameObject.getMapkitItem().getMapkit().getId());
                    }

                    gameObjectDataEntry.add(DataKey.ITEM, gameObject.getMapkitItem().getId());
                    gameObjectDataEntry.add(DataKey.LAYER, String.valueOf(layer));

                    getProperties(gameObject, gameObjectDataEntry);

                    stringBuilder.append(gameObjectDataEntry.stringify()).append('\n');
                }
            }
        });

        return stringBuilder.toString();
    }
}
