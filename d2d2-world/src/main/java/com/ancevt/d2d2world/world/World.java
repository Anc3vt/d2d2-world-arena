
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.norender.NoRenderBackend;
import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.ActorEvent;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.IResettable;
import com.ancevt.d2d2world.gameobject.ISynchronized;
import com.ancevt.d2d2world.gameobject.IdGenerator;
import com.ancevt.d2d2world.gameobject.Parallax;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.Scenery;
import com.ancevt.d2d2world.gameobject.area.Area;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.map.GameMap;
import com.ancevt.d2d2world.map.Room;
import com.ancevt.d2d2world.process.PlayProcessor;
import com.ancevt.d2d2world.sync.ISyncClientDataSender;
import com.ancevt.d2d2world.sync.ISyncDataAggregator;
import com.ancevt.d2d2world.sync.StubSyncClientDataSender;
import com.ancevt.d2d2world.sync.StubSyncDataAggregator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.ancevt.d2d2world.D2D2World.isServer;
import static com.ancevt.d2d2world.constant.AnimationKey.IDLE;

@Slf4j
public class World extends DisplayObjectContainer {

    private final List<IGameObject> gameObjects;
    private final Map<Integer, IGameObject> gameObjectMap;
    private final Layer[] layers;
    private final PlayProcessor playProcessor;
    private final Camera camera;

    private PackedScenery packedSceneryBack;
    private PackedScenery packedSceneryFore;
    private Set<IGameObject> sceneriesBuffer;
    private boolean sceneryPacked;
    private boolean areasVisible;
    private BorderedRect roomRect;
    private GameMap currentMap;
    private Room currentRoom;
    private boolean playing;
    private boolean switchingRoomsNow;
    private ISyncDataAggregator syncDataAggregator;
    private ISyncClientDataSender syncClientDataSender;
    private Overlay overlay;

    public World(@NotNull ISyncDataAggregator syncDataAggregator, @NotNull ISyncClientDataSender syncClientDataSender) {
        this.syncDataAggregator = syncDataAggregator;
        this.syncClientDataSender = syncClientDataSender;

        gameObjectMap = new HashMap<>();
        gameObjects = new CopyOnWriteArrayList<>();
        sceneriesBuffer = new HashSet<>();
        layers = new Layer[Layer.LAYER_COUNT];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new Layer(i);
            add(layers[i]);
        }

        playProcessor = new PlayProcessor(this);
        camera = new Camera(this);
    }

    public World() {
        this(new StubSyncDataAggregator(), new StubSyncClientDataSender());
    }

    public void setSyncDataAggregator(ISyncDataAggregator syncDataAggregator) {
        this.syncDataAggregator = syncDataAggregator;
    }

    public @NotNull ISyncDataAggregator getSyncDataAggregator() {
        return syncDataAggregator;
    }

    public void setSyncClientDataSender(ISyncClientDataSender syncClientDataSender) {
        this.syncClientDataSender = syncClientDataSender;
    }

    public ISyncClientDataSender getSyncClientDataSender() {
        return syncClientDataSender;
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
        dispatchEvent(WorldEvent.builder().type(WorldEvent.WORLD_PROCESS).build());
        playProcessor.process();
        camera.process();
    }

    public GameMap getMap() {
        return currentMap;
    }

    public void setMap(@NotNull GameMap map) {
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

        currentRoom = room;

        update();

        dispatchEvent(WorldEvent.builder()
                .type(WorldEvent.CHANGE_ROOM)
                .room(room)
                .build());
        if (!isServer()) {
            setSceneryPacked(true);
        }

        if (isRoomRectVisible()) {
            setRoomRectVisible(false);
            setRoomRectVisible(true);
        }
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

    public List<IGameObject> getGameObjects() {
        return List.copyOf(gameObjects);
    }

    public List<IGameObject> getSyncGameObjects() {
        return gameObjects.stream().filter(o -> o instanceof ISynchronized).toList();
    }

    public IGameObject getGameObject(int index) {
        return gameObjects.get(index);
    }

    public IGameObject getGameObjectById(int id) {
        return gameObjectMap.get(id);
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

            sceneriesBuffer = gameObjects.stream()
                    .filter(gameObject -> gameObject instanceof Scenery scenery && scenery.isStatic())
                    .collect(Collectors.toSet());

            packedSceneryFore = SceneryPacker.pack(currentRoom, 7, 8);
            getLayer(TARGET_LAYER_INDEX_FG).add(packedSceneryFore);

            removeSceneries();
            gameObjects.removeAll(sceneriesBuffer);
        } else {
            removePackedScenery(packedSceneryBack);
            removePackedScenery(packedSceneryFore);

            //gameObjects.addAll(sceneriesBuffer);
            addSceneries();
            sceneriesBuffer.clear();
        }
    }

    private void removePackedScenery(@NotNull PackedScenery ps) {
        ps.removeFromParent();
        D2D2.getTextureManager().unloadTextureAtlas(ps.getTexture().getTextureAtlas());
        System.gc();
    }

    private void removeSceneries() {
        List<IGameObject> toRemove = new ArrayList<>();

        for (IGameObject gameObject : gameObjects) {
            if (gameObject instanceof final Scenery scenery && scenery.isStatic()) {
                scenery.removeFromParent();
                toRemove.add(gameObject);
            }
        }

        gameObjects.removeAll(toRemove);
    }

    private void addSceneries() {
        for (IGameObject gameObject : getRoom().getGameObjects()) {
            if (!gameObject.hasParent() && gameObject instanceof Scenery scenery && scenery.isStatic()) {
                try {
                    addGameObject(scenery, getRoom().getLayerIndexOfGameObject(gameObject), false);
                } catch (IllegalStateException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public void switchRoomWithActor(String roomIdSwitchTo, Actor actor, float actorX, float actorY) {
        if (switchingRoomsNow) return;

        final Room oldRoom = currentRoom;

        overlay = new Overlay(oldRoom.getWidth(), oldRoom.getHeight());
        overlay.addEventListener(Event.CHANGE, e -> {
            if (overlay.getState() == Overlay.STATE_BLACK) {
                Room targetRoom = currentMap.getRoom(roomIdSwitchTo);
                setRoom(targetRoom);
                setSceneryPacked(true);
                overlay.setSize(targetRoom.getWidth() * 2, targetRoom.getHeight() * 2);
                actor.setXY(actorX, actorY);
                camera.setXY(actorX, actorY);
                addGameObject(actor, 5, false);
                camera.setAttachedTo(actor);
                actor.setAnimation(IDLE);
                actor.dispatchEvent(ActorEvent.builder()
                        .type(ActorEvent.ACTOR_ENTER_ROOM)
                        .roomId(roomIdSwitchTo)
                        .x(actorX)
                        .y(actorY)
                        .build());
            } else if (overlay.getState() == Overlay.STATE_DONE) {
                overlay.removeFromParent();
                switchingRoomsNow = false;
            }
        });

        dispatchEvent(WorldEvent.builder().type(WorldEvent.ROOM_SWITCH_START).build());

        add(overlay);
        removeGameObject(actor, false);
        switchingRoomsNow = true;
        camera.setAttachedTo(null);
        overlay.startIn();
    }

    public void roomSwitchOverlayStartOut() {
        overlay.startOut();
        dispatchEvent(WorldEvent.builder().type(WorldEvent.ROOM_SWITCH_COMPLETE).build());
    }

    public void addGameObject(@NotNull IGameObject gameObject, int layerIndex, boolean updateRoom) {
        for (var o : gameObjects) {
            if (o.getGameObjectId() == gameObject.getGameObjectId())
                throw new IllegalStateException("duplicate game object id: " + gameObject.getGameObjectId() + " " + gameObject + " and  " + o);
        }

        IdGenerator.getInstance().addId(gameObject.getGameObjectId());

        if (gameObject instanceof Parallax parallax && !(D2D2.getBackend() instanceof NoRenderBackend)) {
            parallax.setCamera(getCamera());
        }

        gameObjects.add(gameObject);
        gameObjectMap.put(gameObject.getGameObjectId(), gameObject);

        getLayer(layerIndex).add(gameObject);

        gameObject.setWorld(this);
        gameObject.onAddToWorld(this);

        if (gameObject instanceof ISynchronized) {
            getSyncDataAggregator().newGameObject(gameObject);
        }

        if (updateRoom)
            currentRoom.addGameObject(layerIndex, gameObject);

        dispatchEvent(WorldEvent.builder()
                .type(WorldEvent.ADD_GAME_OBJECT)
                .gameObject(gameObject)
                .build());
    }


    public void removeGameObject(@NotNull IGameObject gameObject, boolean updateRoom) {
        gameObjects.remove(gameObject);
        gameObjectMap.remove(gameObject.getGameObjectId());

        if (gameObject instanceof Parallax parallax && !(D2D2.getBackend() instanceof NoRenderBackend)) {
            parallax.setCamera(null);
        }

        for (int layerIndex = 0; layerIndex < layers.length; layerIndex++) {
            Layer layer = layers[layerIndex];
            if (layer == gameObject.getParent()) {
                layer.remove(gameObject);
                gameObject.setWorld(null);
                getSyncDataAggregator().remove(gameObject);
                if (updateRoom) currentRoom.removeGameObject(layerIndex, gameObject);
            }
        }

        dispatchEvent(WorldEvent.builder()
                .type(WorldEvent.REMOVE_GAME_OBJECT)
                .gameObject(gameObject)
                .build());
    }

    public Layer getLayer(int layerNumber) {
        return layers[layerNumber];
    }

    public Layer getLayerByGameObject(@NotNull IGameObject gameObject) {
        if (gameObject.getParent() instanceof Layer layer) {
            return layer;
        }

        throw new IllegalStateException("Game object has no layer. Game object: " + gameObject.toString());
    }

    public void reset() {
        playProcessor.reset();
        gameObjects.forEach(o -> {
            if (!(o instanceof PlayerActor) && o instanceof IResettable r) {
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
            removeGameObject(gameObject, false);
        }

        for (Layer layer : layers) {
            layer.removeAllChildren();
        }
    }

    public void actorAttack(@NotNull Weapon weapon) {
        weapon.shoot(this);
    }

    public boolean isSwitchingRoomsNow() {
        return switchingRoomsNow;
    }

    @Override
    public String toString() {
        return "World{" +
                "currentRoom=" + currentRoom.getId() +
                ", gameObjects=" + gameObjects.size() +
                ", playing=" + playing +
                '}';
    }

}























