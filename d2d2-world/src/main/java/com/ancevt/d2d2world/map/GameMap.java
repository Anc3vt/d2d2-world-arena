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

import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.exception.GameException;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.world.Layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GameMap {

    private static final int MAX_GAME_OBJECTS = Integer.MAX_VALUE;

    private final Map<String, Room> rooms;
    private String startRoomName;
    private String name;
    private float gravity;
    private String mapkitUids;
    private String mapkitNames;
    private Music music;

    public GameMap() {
        rooms = new TreeMap<>();
        mapkitUids = "";
    }

    @Property
    public String getMapkitUids() {
        return mapkitUids;
    }

    @Property
    public void setMapkitUids(String mapkits) {
        this.mapkitUids = mapkits;
    }

    @Property
    public String getMapkitNames() {
        return mapkitNames;
    }

    @Property
    public void setMapkitNames(String mapkitNames) {
        this.mapkitNames = mapkitNames;
    }

    @Property
    public void setName(String name) {
        this.name = name;
    }

    @Property
    public String getName() {
        return name;
    }

    @Property
    public float getGravity() {
        return gravity;
    }

    @Property
    public String getStartRoomName() {
        return startRoomName;
    }

    @Property
    public void setStartRoomName(String startRoomName) {
        this.startRoomName = startRoomName;
    }

    @Property
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public List<IGameObject> getAllGameObjectsFromAllRooms() {
        List<IGameObject> result = new ArrayList<>();
        rooms.forEach((key, room) -> result.addAll(Arrays.stream(room.getGameObjects()).toList()));
        return result;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public Music getMusic() {
        return music;
    }

    public void putRoom(Room room) {
        rooms.put(room.getName(), room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room.getName());
    }

    public int getRoomCount() {
        return rooms.size();
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Room[] getRooms() {
        return rooms.values().toArray(new Room[0]);
    }

    public final IGameObject getGameObjectById(int gameObjectId) {
        for (String roomId : rooms.keySet()) {
            Room room = rooms.get(roomId);

            for (int l = 0; l < Layer.LAYER_COUNT; l++) {

                for (int i = 0; i < room.getGameObjectsCount(l); i++) {
                    IGameObject gameObject = room.getGameObject(l, i);
                    if (gameObject.getGameObjectId() == gameObjectId)
                        return gameObject;
                }

            }
        }

        return null;
    }

    public final int getNextFreeGameObjectId() {
        for (int i = 1; i < MAX_GAME_OBJECTS; i++) {
            final IGameObject gameObject = getGameObjectById(i);
            if (gameObject == null) return i;
        }
        throw new GameException("MAX_GAME_OBJECTS is reached");
    }

    @Override
    public String toString() {
        return "GameMap{" +
                "rooms=" + rooms +
                ", startRoom=" + startRoomName +
                ", mapkits='" + mapkitUids + '\'' +
                ", name='" + name + '\'' +
                ", gravity=" + gravity +
                ", mapkits=" + mapkitUids +
                ", music=" + music +
                '}';
    }

    public Room getStartRoom() {
        return getRoom(getStartRoomName());
    }
}