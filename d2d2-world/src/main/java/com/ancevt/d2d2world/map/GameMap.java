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
package com.ancevt.d2d2world.map;

import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.world.Layer;

import java.util.*;

public class GameMap {

    private static final int MAX_GAME_OBJECTS = Integer.MAX_VALUE;

    private final Map<String, Room> rooms;
    private String startRoomName;
    private String name;
    private float gravity;
    private String mapkitNames;
    private Music music;

    public GameMap() {
        rooms = new TreeMap<>();
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
        rooms.put(room.getId(), room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room.getId());
    }

    public int getRoomCount() {
        return rooms.size();
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Optional<Room> getRoomByGameObject(IGameObject gameObject) {
        for (Room room : rooms.values()) {
            for (IGameObject o : room.getGameObjects()) {
                if(o == gameObject) return Optional.of(room);
            }
        }

        System.err.println("WARNING: No room of gameObject " + gameObject);

        return Optional.empty();
    }

    public List<Room> getRooms() {
        return rooms.values().stream().toList();
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

    @Override
    public String toString() {
        return "GameMap{" +
                "rooms=" + rooms +
                ", startRoom=" + startRoomName +
                ", name='" + name + '\'' +
                ", gravity=" + gravity +
                ", music=" + music +
                '}';
    }

    public Room getStartRoom() {
        return getRoom(getStartRoomName());
    }
}
