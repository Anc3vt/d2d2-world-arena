
package com.ancevt.d2d2world.map;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.DataEntryLoader;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.IdGenerator;
import com.ancevt.d2d2world.mapkit.Mapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.Layer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.data.Properties.setProperties;

public class MapIO {

    private static final Logger log = LoggerFactory.getLogger(MapIO.class);

    private static String mapsDirectory;
    private static String mapFileName;
    private static String mapkitsDirectory;

    public static void setMapFileName(String mapFileName) {
        MapIO.mapFileName = mapFileName;
    }

    public static String getMapFileName() {
        return mapFileName;
    }

    public static void setMapkitsDirectory(String mapkitsDirectory) {
        MapIO.mapkitsDirectory = mapkitsDirectory;
    }

    public static String getMapkitsDirectory() {
        return mapkitsDirectory;
    }

    public static void setMapsDirectory(String mapsDirectory) {
        MapIO.mapsDirectory = mapsDirectory;
    }

    public static String getMapsDirectory() {
        return mapsDirectory;
    }

    public static @NotNull GameMap load(String mapFileName) throws IOException {
        IdGenerator.getInstance().clear();

        log.debug("load map: {}", mapFileName);

        DataEntry[] dataEntries = DataEntryLoader.load(MapIO.mapsDirectory + mapFileName);

        log.debug("loaded <g>{}<> data entries", dataEntries.length);

        GameMap map = new GameMap();

        Room room = null;

        for (DataEntry dataEntry : dataEntries) {
            if (dataEntry.containsKey(DataKey.MAP)) {
                setProperties(map, dataEntry);

                String mapkitNames = dataEntry.getString(DataKey.MAPKIT_NAMES);
                String[] splitNames = mapkitNames.split(",");
                for (int i = 0; i < splitNames.length; i++) {
                    MapkitManager.getInstance().load(splitNames[i]);
                }
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

            if (IdGenerator.getInstance().contains(gameObjectId)) {
                int newGameObjectId = IdGenerator.getInstance().getNewId();
                log.warn("duplicate game object id {}, change to {}", gameObjectId, newGameObjectId);
                gameObjectId = newGameObjectId;
            }

            if (gameObjectId == 0) gameObjectId = IdGenerator.getInstance().getNewId();

            String mapkitItemId = dataEntry.getString(DataKey.ITEM);
            int layer = dataEntry.getInt(DataKey.LAYER);

            String mapkitName = dataEntry.getString(DataKey.MAPKIT);

            Mapkit mapkit = MapkitManager.getInstance().getMapkit(mapkitName);

            if (room == null) throw new IllegalStateException("room undefined");

            MapkitItem mapkitItem = mapkit.getItemById(mapkitItemId);
            if (mapkitItem != null) {
                IGameObject gameObject = (IGameObject) setProperties(mapkitItem.createGameObject(gameObjectId), dataEntry);
                room.addGameObject(layer, gameObject);
            }

            IdGenerator.getInstance().addId(gameObjectId);
        }

        return map;
    }

    public static @NotNull String toString(GameMap map) {
        StringBuilder stringBuilder = new StringBuilder();

        DataEntry mapDataEntry = DataEntry.newInstance();
        mapDataEntry.add(DataKey.MAP);

        getProperties(map, mapDataEntry);

        stringBuilder.append(mapDataEntry.stringify()).append('\n');

        map.getRooms().forEach(room -> {
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

    public static @NotNull String save(GameMap map, String mapFileName) {
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
