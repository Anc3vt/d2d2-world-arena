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
package com.ancevt.d2d2world.server.scene;

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.norender.NoRenderBackend;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.DefaultMaps;
import com.ancevt.d2d2world.gameobject.IDamaging;
import com.ancevt.d2d2world.gameobject.IResettable;
import com.ancevt.d2d2world.gameobject.IdGenerator;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.area.AreaHook;
import com.ancevt.d2d2world.gameobject.area.AreaSpawn;
import com.ancevt.d2d2world.gameobject.pickup.WeaponPickup;
import com.ancevt.d2d2world.gameobject.weapon.StandardWeapon;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.map.GameMap;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.net.dto.server.DeathDto;
import com.ancevt.d2d2world.net.dto.server.DestroyableBoxDestroyDto;
import com.ancevt.d2d2world.net.dto.server.PlayerEnterRoomStartResponseDto;
import com.ancevt.d2d2world.net.dto.server.SetRoomDto;
import com.ancevt.d2d2world.net.dto.server.SpawnEffectDto;
import com.ancevt.d2d2world.net.sync.SyncDataAggregator;
import com.ancevt.d2d2world.server.content.ServerContentManager;
import com.ancevt.d2d2world.server.player.Player;
import com.ancevt.d2d2world.server.service.GeneralService;
import com.ancevt.d2d2world.sync.StubSyncClientDataSender;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2world.constant.AnimationKey.IDLE;
import static com.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessagePlayerAttack;
import static com.ancevt.d2d2world.server.config.ServerConfig.CONFIG;
import static com.ancevt.d2d2world.server.config.ServerConfig.DEBUG_FORCED_SPAWN_AREA;
import static com.ancevt.d2d2world.server.content.ServerContentManager.MODULE_CONTENT_MANAGER;
import static com.ancevt.d2d2world.server.player.ServerPlayerManager.PLAYER_MANAGER;
import static com.ancevt.d2d2world.server.service.ServerSender.SENDER;

@Slf4j
public class ServerWorldScene {

    public static final ServerWorldScene SERVER_WORLD_SCENE = new ServerWorldScene();
    /**
     * roomId => world
     */
    private final Map<String, World> worlds;
    private GameMap gameMap;
    private FpsMeter fpsMeter;

    private final Map<Integer, PlayerActor> playerActorMap = new ConcurrentHashMap<>();
    private Stage stage;

    private ServerWorldScene() {
        MapIO.setMapkitsDirectory("data/mapkits/");
        MapIO.setMapsDirectory("data/maps/");

        worlds = new ConcurrentHashMap<>();
    }

    private void clear() {
        worlds.values().forEach(world -> {
            world.setPlaying(false);
            world.setSceneryPacked(false);
            world.removeEventListener(this, WorldEvent.ACTOR_DEATH);
            world.removeEventListener(this, WorldEvent.ADD_GAME_OBJECT);
            world.removeEventListener(this, WorldEvent.ACTOR_ATTACK);
            world.removeEventListener(this, WorldEvent.REMOVE_GAME_OBJECT);
            world.removeEventListener(this, WorldEvent.BULLET_DOOR_TELEPORT);
            world.removeEventListener(this, WorldEvent.DESTROYABLE_BOX_DESTROY);
            world.clear();
            world.removeFromParent();
        });
        worlds.clear();
    }

    private void reset() {
        worlds.values().forEach(World::reset);
    }

    public void start() {
        stage = D2D2.init(new NoRenderBackend(900, 600));
        stage.addEventListener(this, Event.ENTER_FRAME, this::root_eachFrame);
        fpsMeter = new FpsMeter();
        stage.add(fpsMeter);
        Async.run(D2D2::loop);
    }

    public int getFps() {
        return D2D2.getBackend().getFps();
    }

    @SneakyThrows(IOException.class)
    public void loadMap(String mapName) {
        reset();
        clear();

        DefaultMaps.clear();

        if (MODULE_CONTENT_MANAGER.containsMap(mapName)) {
            ServerContentManager.Map scmMap = MODULE_CONTENT_MANAGER.getMapByName(mapName);
            long timeBefore = System.currentTimeMillis();
            gameMap = MapIO.load(scmMap.filename());
            log.info("Map '{}' loaded {}ms", mapName, System.currentTimeMillis() - timeBefore);


            CONFIG.ifContains(DEBUG_FORCED_SPAWN_AREA, value -> {
                gameMap.getAllGameObjectsFromAllRooms().stream()
                        .filter(gameObject -> gameObject instanceof AreaSpawn areaSpawn && areaSpawn.getName().equals(value))
                        .findAny().ifPresent(debugForcedSpawnArea -> {
                            gameMap.getAllGameObjectsFromAllRooms().stream()
                                    .filter(gameObject -> gameObject instanceof AreaSpawn && gameObject != debugForcedSpawnArea)
                                    .forEach(gameObject -> {
                                        AreaSpawn areaSpawn = (AreaSpawn) gameObject;
                                        areaSpawn.setEnabled(false);
                                    });
                        });
            });

            gameMap.getRooms().forEach(room -> {
                World world = new World(new SyncDataAggregator(), new StubSyncClientDataSender());
                world.addEventListener(this, WorldEvent.ACTOR_ATTACK, this::world_actorAttack);
                world.addEventListener(this, WorldEvent.ACTOR_DEATH, this::world_actorDeath);
                world.addEventListener(this, WorldEvent.ADD_GAME_OBJECT, this::world_addGameObject);
                world.addEventListener(this, WorldEvent.REMOVE_GAME_OBJECT, this::world_removeGameObject);
                world.addEventListener(this, WorldEvent.BULLET_DOOR_TELEPORT, this::world_bulletDoorTeleport);
                world.addEventListener(this, WorldEvent.DESTROYABLE_BOX_DESTROY, this::world_destroyableDestroy);
                world.setMap(gameMap);
                world.setRoom(room);
                world.setSceneryPacked(true);
                world.setPlaying(true);
                worlds.put(room.getId(), world);
                stage.add(world);
            });
        } else {
            log.warn("No such map \"{}\"", mapName);
        }
    }

    private void world_actorAttack(Event event) {
        var e = (WorldEvent) event;
        if (e.getActor() instanceof PlayerActor playerActor) {
            int playerId = playerActor.getPlayerId();
            SENDER.sendToAllOfRoom(createMessagePlayerAttack(playerId), playerActor.getWorld().getRoom().getId());
        }
    }

    private void world_destroyableDestroy(Event event) {
        var e = (WorldEvent) event;
        World world = (World) e.getSource();
        SENDER.sendToAllOfRoom(DestroyableBoxDestroyDto.builder()
                .destroyableGameObjectId(e.getGameObject().getGameObjectId())
                .build(), world.getRoom().getId()
        );
    }

    private void world_bulletDoorTeleport(@NotNull Event event) {
        var e = (WorldEvent) event;

        Weapon.Bullet b = e.getBullet();

        World oldWorld = (World) event.getSource();
        oldWorld.removeGameObject(b, false);

        World world = worlds.get(e.getRoomId());
        b.setXY(e.getX(), e.getY());
        world.addGameObject(b, 5, false);

    }

    private void world_addGameObject(Event event) {
        var e = (WorldEvent) event;
        if (e.getGameObject() instanceof PlayerActor playerActor) {

            World world = (World) e.getSource();

            SENDER.sendToAllOfRoom(
                    SpawnEffectDto.builder()
                            .x(playerActor.getX())
                            .y(playerActor.getY())
                            .build(),
                    world.getRoom().getId()
            );
        }
    }

    private void world_removeGameObject(Event event) {
        var e = (WorldEvent) event;
        if (e.getGameObject() instanceof PlayerActor playerActor) {
            World world = (World) e.getSource();

            if (world.getRoom() != null) {
                SENDER.sendToAllOfRoom(
                        SpawnEffectDto.builder()
                                .x(playerActor.getX())
                                .y(playerActor.getY())
                                .build(),
                        world.getRoom().getId()
                );
            }
        }
    }

    private synchronized void root_eachFrame(Event event) {
        worlds.forEach((roomId, world) -> {
            var agg = world.getSyncDataAggregator();
            if (agg.hasData()) {
                SENDER.sendToAllOfRoom(agg.pullSyncDataMessage(), roomId);
            }
        });
    }

    /**
     * Calls from {@link GeneralService}
     */
    public void playerController(int playerId, int controllerState) {
        PlayerActor playerActor = playerActorMap.get(playerId);
        if (playerActor != null) {
            playerActor.getController().applyState(controllerState);
        }
    }

    /**
     * Calls from {@link GeneralService}
     */
    public void playerXY(int playerId, float x, float y) {
        PlayerActor playerActor = playerActorMap.get(playerId);
        if (playerActor != null) {
            playerActor.setXY(x, y);
        }
    }

    /**
     * Calls from {@link GeneralService}
     */
    public void playerAimXY(int playerId, float x, float y) {
        PlayerActor playerActor = playerActorMap.get(playerId);
        if (playerActor != null) {
            playerActor.setAimXY(x, y);
        }
    }

    /**
     * Calls from {@link GeneralService}
     */
    public void playerWeaponSwitch(int playerId, int delta) {
        PlayerActor playerActor = playerActorMap.get(playerId);
        if (playerActor != null) {
            if (delta > 0) playerActor.nextWeapon();
            else playerActor.prevWeapon();
        }
    }

    /**
     * Calls from {@link GeneralService}
     */
    public int spawnPlayerFirstTime(@NotNull Player player, @NotNull String mapkitItemId) {
        PlayerActor playerActor = SERVER_WORLD_SCENE.createPlayerActor(player, mapkitItemId);
        playerActor.setVisible(true);
        playerActor.setAnimation(IDLE);
        sendGameObjectsSyncData(player.getId());
        return playerActor.getGameObjectId();
    }

    /**
     * Calls from {@link GeneralService}
     */
    public void changePlayerRoom(int playerId, String roomId, float x, float y) {
        PLAYER_MANAGER.getPlayerById(playerId).ifPresent(player -> player.setRoomId(roomId));
        PlayerActor playerActor = playerActorMap.get(playerId);
        if (playerActor.isOnWorld()) {
            playerActor.getWorld().removeGameObject(playerActor, false);
        }

        World worldToEnter = worlds.get(roomId);
        playerActor.setXY(x, y);
        playerActor.setVisible(false);
        worldToEnter.addGameObject(playerActor, 5, false);
        SENDER.sendToPlayer(playerId, PlayerEnterRoomStartResponseDto.builder().build());
    }

    /**
     * Calls from {@link GeneralService}
     */
    public void playerHook(int playerId, int hookGameObjectId) {
        getPlayerActorByPlayerId(playerId).ifPresent(playerActor -> {
            World world = getWorldByGameObjectId(hookGameObjectId);
            if (world != null && world.getGameObjectById(hookGameObjectId) instanceof AreaHook hook) {
                playerActor.setHook(hook);
            }
        });
    }

    public void sendGameObjectsSyncData(int playerId) {
        getPlayerActorByPlayerId(playerId).ifPresent(playerActor -> {
            World world = playerActor.getWorld();
            world.getSyncGameObjects().forEach(o -> {
                byte[] bytes = SyncDataAggregator.createSyncMessageOf(o);
                if (bytes.length > 0) SENDER.sendToPlayer(playerId, bytes);
            });
        });
    }

    public Optional<PlayerActor> getPlayerActorByPlayerId(int playerId) {
        return Optional.ofNullable(playerActorMap.get(playerId));
    }

    public PlayerActor createPlayerActor(@NotNull Player player, @NotNull String mapkitItemId) {
        MapkitItem mapkitItem = MapkitManager.getInstance().getMapkit(BuiltInMapkit.NAME).getItemById(mapkitItemId);
        PlayerActor playerActor = (PlayerActor) mapkitItem.createGameObject(IdGenerator.getInstance().getNewId());
        playerActor.setHumanControllable(true);
        playerActor.getController().setEnabled(true);
        playerActor.setPlayerColorValue(player.getColor());
        playerActor.setName("playerActor_" + player.getName());
        playerActor.setPlayerName(player.getName());
        playerActor.setPlayerId(player.getId());

        World newWorld = spawnPlayerActorToRandomSpawnPoint(player.getId(), playerActor);
        playerActor.setVisible(false);
        player.setRoomId(newWorld.getRoom().getId());

        playerActorMap.put(player.getId(), playerActor);

        log.info("Add player actor {}", playerActor);

        return playerActor;
    }

    public World spawnPlayerActorToRandomSpawnPoint(int playerId, PlayerActor playerActor) {
        while (true) {
            World world = getRandomWorld();
            List<AreaSpawn> areas = new ArrayList<>();
            world.getGameObjects().forEach(gameObject -> {
                if (gameObject instanceof AreaSpawn areaSpawn && areaSpawn.isEnabled()) {
                    areas.add(areaSpawn);
                }
            });

            if (!areas.isEmpty()) {
                AreaSpawn areaSpawn = areas.get(new Random().nextInt(areas.size()));
                if (playerActor.isOnWorld()) {
                    playerActor.getWorld().removeGameObject(playerActor, false);
                }

                PLAYER_MANAGER.getPlayerById(playerId).ifPresent(player -> player.setRoomId(getRoomIdByWorld(world)));

                float w = new Random().nextFloat(areaSpawn.getWidth());
                float h = new Random().nextFloat(areaSpawn.getHeight());
                playerActor.setXY(areaSpawn.getX() + w, areaSpawn.getY() + h);
                world.addGameObject(playerActor, 5, false);

                SENDER.sendToPlayer(playerId, SetRoomDto.builder()
                        .roomId(world.getRoom().getId())
                        .cameraX(playerActor.getX())
                        .cameraY(playerActor.getY())
                        .build());

                SENDER.sendToAllOfRoom(
                        SpawnEffectDto.builder()
                                .x(playerActor.getX())
                                .y(playerActor.getY())
                                .build(),
                        world.getRoom().getId()
                );

                return world;
            }
        }
    }

    private World getRandomWorld() {
        int randomIndex = new Random().nextInt(worlds.size());
        int i = 0;
        for (World world : worlds.values()) {
            if (i == randomIndex) {
                return world;
            }
            i++;
        }
        throw new IllegalStateException("Cannot return random world");
    }

    public void removePlayer(@NotNull Player player) {
        PlayerActor playerActor = playerActorMap.remove(player.getId());
        if (playerActor != null && playerActor.isOnWorld()) {
            playerActor.getWorld().removeGameObject(playerActor, false);
        }

        log.info("Remove player actor {}", playerActor);
    }

    public int getPlayerActorGameObjectId(int playerId) {
        Holder<Integer> gameObjectId = new Holder<>(0);
        getPlayerActorByPlayerId(playerId).ifPresent(playerActor -> gameObjectId.setValue(playerActor.getGameObjectId()));
        return gameObjectId.getValue();
    }

    private void world_actorDeath(Event event) {
        var e = (WorldEvent) event;

        World world = (World) e.getSource();

        final Holder<Integer> deadPlayerId = new Holder<>(0);
        final Holder<Integer> killerPlayerId = new Holder<>(0);

        // get victim player id
        if (world.getGameObjectById(e.getDeadActorGameObjectId()) instanceof PlayerActor playerActor) {
            playerActorMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().getGameObjectId() == playerActor.getGameObjectId())
                    .findAny()
                    .ifPresent(id -> deadPlayerId.setValue(id.getKey()));
        }

        // get killer player id
        if (world.getGameObjectById(e.getKillerGameObjectId()) instanceof PlayerActor playerActor) {
            playerActorMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().getGameObjectId() == playerActor.getGameObjectId())
                    .findAny()
                    .ifPresent(id -> killerPlayerId.setValue(id.getKey()));
        }

        if (deadPlayerId.getValue() != 0) {
            if (killerPlayerId.getValue() != 0)
                PLAYER_MANAGER.getPlayerById(killerPlayerId.getValue()).orElseThrow().incrementFrags();
            else
                PLAYER_MANAGER.getPlayerById(deadPlayerId.getValue()).orElseThrow().decrementFrags();

            DeathDto deathDto = DeathDto.builder()
                    .deadPlayerId(deadPlayerId.getValue())
                    .killerPlayerId(killerPlayerId.getValue())
                    .build();
            SENDER.sendToAll(deathDto);

            PlayerActor deadPlayerActor = playerActorMap.get(deadPlayerId.getValue());
            if (deadPlayerActor != null) {

                // weapon pickup drop
                Weapon weapon = deadPlayerActor.getCurrentWeapon();
                if (weapon.getClass() != StandardWeapon.class) {
                    WeaponPickup weaponPickup = BuiltInMapkit.createWeaponPickupMapkitItem(weapon);
                    weaponPickup.setXY(deadPlayerActor.getX(), deadPlayerActor.getY());
                    weaponPickup.setCollisionEnabled(true);
                    world.addGameObject(weaponPickup, 5, false);
                }

                // resurrect player
                Async.runLater(2, TimeUnit.SECONDS, () -> {
                    world.removeGameObject(deadPlayerActor, false);
                });

                Async.runLater(5, TimeUnit.SECONDS, () -> {
                    // TODO: extract to separate method
                    deadPlayerActor.repair();
                    spawnPlayerActorToRandomSpawnPoint(deadPlayerId.getValue(), deadPlayerActor);
                });
            }
        }
    }

    private String getRoomIdByWorld(World world) {
        for (Map.Entry<String, World> entry : worlds.entrySet()) {
            if (entry.getValue().equals(world)) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("No room for world: " + world);
    }

    public void playerHealthReport(int connectionId, int healthValue, int damagingGameObjectId) {
        World world = getPlayerActorByPlayerId(connectionId).orElseThrow().getWorld();

        IDamaging damagingGameObject = (IDamaging) world.getGameObjectById(damagingGameObjectId);

        if (damagingGameObject != null) {
            getPlayerActorByPlayerId(connectionId).ifPresent(
                    playerActor -> playerActor.setHealthBy(healthValue, damagingGameObject, false)
            );
        }

    }

    public World getWorldByGameObjectId(int gameObjectId) {
        Holder<World> result = new Holder<>();
        worlds.values().stream()
                .filter(w -> w.getGameObjectById(gameObjectId) != null)
                .findAny()
                .ifPresent(result::setValue);
        return result.getValue();
    }

    public void instantSwitchRoomForPlayerActor(int playerId, String roomId, float x, float y) {
        World world = worlds.get(roomId);

        if (world == null) throw new IllegalStateException("No such room '" + roomId + "'");

        getPlayerActorByPlayerId(playerId).ifPresentOrElse(playerActor -> {

            PLAYER_MANAGER.getPlayerById(playerId).ifPresent(player -> player.setRoomId(roomId));

            if (playerActor.isOnWorld()) {
                playerActor.getWorld().removeGameObject(playerActor, false);
            }

            playerActor.setXY(x, y);
            world.addGameObject(playerActor, 5, false);

            SENDER.sendToPlayer(playerId,
                    SetRoomDto.builder()
                            .roomId(roomId)
                            .cameraX(x)
                            .cameraY(y)
                            .build());

        }, () -> {
            throw new IllegalStateException("player hasn't player actor, player id: " + playerId);
        });
    }

    public void resetAllResettableGameObjects() {
        worlds.values().forEach(w -> {
            w.getGameObjects().forEach(gameObject -> {
                if (gameObject instanceof IResettable resettable) resettable.reset();
            });
        });
    }
}







































