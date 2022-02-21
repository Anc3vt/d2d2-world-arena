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
package ru.ancevt.d2d2world.world;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.common.BorderedRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.exception.NotImplementedException;
import ru.ancevt.d2d2world.gameobject.Actor;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.gameobject.IResettable;
import ru.ancevt.d2d2world.gameobject.Scenery;
import ru.ancevt.d2d2world.gameobject.area.Area;
import ru.ancevt.d2d2world.gameobject.weapon.Weapon;
import ru.ancevt.d2d2world.map.GameMap;
import ru.ancevt.d2d2world.map.Room;
import ru.ancevt.d2d2world.process.PlayProcessor;

import java.util.ArrayList;
import java.util.List;

public class World extends DisplayObjectContainer {

    private final List<IGameObject> gameObjects;
    private final Layer[] layers;
    private final PlayProcessor playProcessor;
    private final Camera camera;

    private PackedScenery packedSceneryBack;
    private PackedScenery packedSceneryFore;
    private boolean sceneryPacked;
    private boolean areasVisible;
    private BorderedRect roomRect;
    private GameMap currentMap;
    private Room currentRoom;
    private boolean playing;
    private boolean switchingRoomsNow;

    public World() {
        gameObjects = new ArrayList<>();
        layers = new Layer[Layer.LAYER_COUNT];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new Layer(i);
            add(layers[i]);
        }

        playProcessor = new PlayProcessor(this);
        camera = new Camera(this);
    }

    public PlayProcessor getPlayProcessor() {
        return playProcessor;
    }

    public Room getRoom() {
        return currentRoom;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }


    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void onEachFrame() {
        if (!isPlaying()) return;

        playProcessor.process();
        camera.process();
    }

    public GameMap getMap() {
        return currentMap;
    }

    public void setMap(GameMap map) {
        removeAllGameObjects();
        this.currentMap = map;

        playProcessor.setGravity(map.getGravity());
        setRoom(currentMap.getStartRoom());
    }

    public void setRoom(Room room) {
        setSceneryPacked(false);
        removeAllGameObjects();

        for (int layer = 0; layer < Layer.LAYER_COUNT; layer++) {
            int objectCount = room.getGameObjectsCount(layer);
            for (int index = 0; index < objectCount; index++) {
                IGameObject gameObject = room.getGameObject(layer, index);

                if (!areasVisible && gameObject instanceof Area) {
                    gameObject.setVisible(false);
                }

                addGameObject(gameObject, layer, false);
            }
        }

        camera.setBounds(room.getWidth(), room.getHeight());

        if (isRoomRectVisible()) {
            setRoomRectVisible(false);
            setRoomRectVisible(true);
        }

        currentRoom = room;

        update();

        dispatchEvent(new WorldEvent(WorldEvent.CHANGE_ROOM, this, room));
    }

    public void update() {
        D2D2.getStage().getRoot().setBackgroundColor(currentRoom.getBackgroundColor());
        playProcessor.setGravity(currentMap.getGravity());
        camera.setBounds(currentRoom.getWidth(), currentRoom.getHeight());
    }

    public void setRoomRectVisible(boolean value) {
        if (value == isRoomRectVisible()) return;

        if (value) {
            roomRect = new BorderedRect(null, Color.DARK_BLUE);

            if (currentMap != null) {
                final int roomWidth = currentRoom.getWidth();
                final int roomHeight = currentRoom.getHeight();

                roomRect = new BorderedRect(
                        roomWidth,
                        roomHeight,
                        null,
                        Color.DARK_BLUE
                );
                add(roomRect);
            }
        } else {
            if (roomRect.hasParent()) remove(roomRect);
            roomRect = null;
        }
    }

    public boolean isRoomRectVisible() {
        return roomRect != null;
    }

    public void setAreasVisible(boolean value) {
        this.areasVisible = value;

        for (int i = 0; i < gameObjects.size(); i++) {
            final IGameObject o = getGameObject(i);
            if (o instanceof final Area a) {
                a.setVisible(value);
            }
        }
    }

    public boolean isAreasVisible() {
        return areasVisible;
    }

    public IGameObject getGameObject(int index) {
        return gameObjects.get(index);
    }

    public int getGameObjectCount() {
        return gameObjects.size();
    }

    public final boolean isSceneryPacked() {
        return sceneryPacked;
    }

    public final void setSceneryPacked(boolean sceneryPacked) {
        if (this.sceneryPacked == sceneryPacked) return;

        this.sceneryPacked = sceneryPacked;

        final int TARGET_LAYER_INDEX_BG = 0;
        final int TARGET_LAYER_INDEX_FG = 8;

        if (sceneryPacked) {
            packedSceneryBack = SceneryPacker.pack(currentRoom, 0, 4);
            getLayer(TARGET_LAYER_INDEX_BG).add(packedSceneryBack);

            packedSceneryFore = SceneryPacker.pack(currentRoom, 7, 8);
            getLayer(TARGET_LAYER_INDEX_FG).add(packedSceneryFore);

            removeSceneries();
        } else {
            removePackedScenery(packedSceneryBack);
            removePackedScenery(packedSceneryFore);
            addSceneries();
        }
    }

    private void removePackedScenery(PackedScenery ps) {
        ps.removeFromParent();
        D2D2.getTextureManager().unloadTextureAtlas(ps.getTexture().getTextureAtlas());
        System.gc();
    }

    private void removeSceneries() {
        List<IGameObject> toRemove = new ArrayList<>();

        for (IGameObject gameObject : gameObjects) {
            if (gameObject instanceof final Scenery s) {
                s.removeFromParent();
                toRemove.add(gameObject);
            }
        }

        gameObjects.removeAll(toRemove);
    }

    private void addSceneries() {
        for (IGameObject gameObject : getRoom().getGameObjects()) {
            if (!gameObject.hasParent()) {
                addGameObject(gameObject, getRoom().getLayerIndexOfGameObject(gameObject), false);
            }
        }
    }

    public void switchRoom(String roomIdSwitchTo, float actorX, float actorY) {
        if (switchingRoomsNow) return;

        final Room oldRoom = currentRoom;

        Overlay overlay = new Overlay(oldRoom.getWidth(), oldRoom.getHeight());
        overlay.addEventListener(Event.CHANGE, e -> {
            if (overlay.getState() == Overlay.STATE_BLACK) {
                setRoom(currentMap.getRoom(roomIdSwitchTo));
                overlay.startOut();
            } else if (overlay.getState() == Overlay.STATE_DONE) {
                overlay.removeFromParent();
                switchingRoomsNow = false;
            }
        });

        add(overlay);
        switchingRoomsNow = true;
        overlay.startIn();
    }

    public void addGameObject(IGameObject gameObject, int layerIndex, boolean updateRoom) {
        gameObjects.add(gameObject);
        getLayer(layerIndex).add(gameObject);

        gameObject.setWorld(this);

        if (updateRoom)
            currentRoom.addGameObject(layerIndex, gameObject);
    }

    public void removeGameObject(IGameObject gameObject, boolean updateRoom) {
        gameObjects.remove(gameObject);
        for (int layerIndex = 0; layerIndex < layers.length; layerIndex++) {
            Layer layer = layers[layerIndex];
            if (layer == gameObject.getParent()) {
                layer.remove(gameObject);
                if (updateRoom) currentRoom.removeGameObject(layerIndex, gameObject);
            }
        }
    }

    public void resetGameObjects() {
        for (IGameObject gameObject : gameObjects) {
            if (gameObject instanceof IResettable resettable) {
                resettable.reset();
            }
        }
    }

    public Layer getLayer(int layerNumber) {
        return layers[layerNumber];
    }

    public Layer getLayerByGameObject(IGameObject gameObject) {
        if (gameObject.getParent() instanceof Layer layer) {
            return layer;
        }

        throw new IllegalStateException("Game object has no layer. Game object: " + gameObject.toString());
    }

    public void reset() {
        gameObjects.forEach(o->{
            if(o instanceof IResettable r) {
                r.reset();
            }
        });
    }

    public void clear() {
        setPlaying(false);
        gameObjects.forEach(o -> {
            if (o instanceof IResettable r) r.reset();
        });
        setSceneryPacked(false);
        setAreasVisible(false);
        camera.setXY(0, 0);
        currentMap = null;
        currentRoom = null;
        switchingRoomsNow = false;

        removeAllGameObjects();
    }

    private void removeAllGameObjects() {
        while (!gameObjects.isEmpty()) {
            IGameObject gameObject = gameObjects.remove(0);
            if (gameObject.hasParent())
                gameObject.getParent().remove(gameObject);
        }

        for (Layer layer : layers) {
            layer.removeAllChildren();
        }
    }

    public void actorAttack(Actor actor, Weapon weapon) {
        throw new NotImplementedException();
    }

    public boolean isSwitchingRoomsNow() {
        return switchingRoomsNow;
    }

}























