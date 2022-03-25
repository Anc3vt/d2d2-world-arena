package com.ancevt.d2d2world.server.simulation;

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.starter.norender.NoRenderStarter;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.gameobject.IdGenerator;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.map.GameMap;
import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.mapkit.CharacterMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.net.dto.server.DeathDto;
import com.ancevt.d2d2world.net.protocol.SyncDataAggregator;
import com.ancevt.d2d2world.server.content.ServerContentManager;
import com.ancevt.d2d2world.server.player.Player;
import com.ancevt.d2d2world.server.service.GeneralService;
import com.ancevt.d2d2world.sync.ISyncDataAggregator;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2world.server.content.ServerContentManager.MODULE_CONTENT_MANAGER;
import static com.ancevt.d2d2world.server.player.ServerPlayerManager.MODULE_PLAYER_MANAGER;
import static com.ancevt.d2d2world.server.service.ServerSender.MODULE_SENDER;

@Slf4j
public class ServerWorldScene {

    public static final ServerWorldScene MODULE_WORLD_SCENE = new ServerWorldScene();

    private World world;
    private FpsMeter fpsMeter;
    private final ISyncDataAggregator syncDataAggregator;

    private final Map<Integer, PlayerActor> playerActorMap = new ConcurrentHashMap<>();

    private ServerWorldScene() {
        syncDataAggregator = new SyncDataAggregator();
        MapIO.mapkitsDirectory = "data/mapkits/";
        MapIO.mapsDirectory = "data/maps/";
    }

    public void start() {
        Root root = D2D2.init(new NoRenderStarter(900, 600));

        world = new World(syncDataAggregator);
        world.addEventListener(this, WorldEvent.ACTOR_DEATH, this::world_actorDeath);
        root.add(world);

        fpsMeter = new FpsMeter();
        root.add(fpsMeter);

        Async.run(D2D2::loop);
    }

    public World getWorld() {
        return world;
    }

    public int getFps() {
        return fpsMeter.getFramesPerSecond();
    }

    public void loadMap(String mapName) {
        D2D2World.resetGameObjectProperties();

        if (MODULE_CONTENT_MANAGER.containsMap(mapName)) {
            ServerContentManager.Map scmMap = MODULE_CONTENT_MANAGER.getMaps()
                    .stream()
                    .filter(m -> m.name().equals(mapName))
                    .findAny()
                    .orElseThrow();

            try {
                long timeBefore = System.currentTimeMillis();
                world.clear();
                GameMap map = MapIO.load(scmMap.filename());
                world.setMap(map);
                world.setPlaying(true);
                world.addEventListener(Event.EACH_FRAME, this::this_eachFrame);
                log.info("Map '" + mapName + "' loaded {}ms", (System.currentTimeMillis() - timeBefore));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }


        }
    }

    private synchronized void this_eachFrame(Event event) {
        if (syncDataAggregator.hasData()) {
            MODULE_SENDER.sendToAll(syncDataAggregator.pullSyncDataMessage());
        }
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
    public void playerAimXY(int playerId, float x, float y) {
        PlayerActor playerActor = playerActorMap.get(playerId);
        if(playerActor != null) {
            playerActor.setAimXY(x, y);
        }
    }

    /**
     * Calls from {@link GeneralService}
     */
    public void playerWeaponSwitch(int playerId, int delta) {
        PlayerActor playerActor = playerActorMap.get(playerId);
        if(playerActor != null) {
            if(delta > 0) playerActor.nextWeapon(); else playerActor.prevWeapon();
        }
    }

    public PlayerActor getPlayerActor(int playerId) {
        return playerActorMap.get(playerId);
    }

    public void addPlayer(@NotNull Player player) {
        MapkitItem mapkitItem = MapkitManager.getInstance().getByName(CharacterMapkit.NAME).getItem("character_ava");
        PlayerActor playerActor = (PlayerActor) mapkitItem.createGameObject(IdGenerator.INSTANCE.getNewId());
        playerActor.getController().setEnabled(true);
        playerActor.setXY(448, 304);
        world.addGameObject(playerActor, 5, false);
        playerActorMap.put(player.getId(), playerActor);

        //DebugPlayerActorCreator.createTestPlayerActor(playerActor, world);

        log.info("Add player actor {}", playerActor);
    }

    public void removePlayer(@NotNull Player player) {
        PlayerActor playerActor = playerActorMap.remove(player.getId());
        if (playerActor != null) {
            world.removeGameObject(playerActor, false);
        }

        log.info("Remove player actor {}", playerActor);
    }

    public int getPlayerActorGameObjectId(int playerId) {
        PlayerActor playerActor = getPlayerActor(playerId);
        if (playerActor != null) {
            return playerActor.getGameObjectId();
        }
        throw new IllegalStateException("no player actor for player id " + playerId);
    }

    private void world_actorDeath(Event event) {
        var e = (WorldEvent) event;


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
                MODULE_PLAYER_MANAGER.getPlayerById(killerPlayerId.getValue()).orElseThrow().incrementFrags();
            else
                MODULE_PLAYER_MANAGER.getPlayerById(deadPlayerId.getValue()).orElseThrow().decrementFrags();

            DeathDto deathDto = DeathDto.builder()
                    .deadPlayerId(deadPlayerId.getValue())
                    .killerPlayerId(killerPlayerId.getValue())
                    .build();
            MODULE_SENDER.sendToAll(deathDto);

            PlayerActor deadPlayerActor = playerActorMap.get(deadPlayerId.getValue());
            if (deadPlayerActor != null) {
                Async.runLater(5, TimeUnit.SECONDS, () -> {
                    deadPlayerActor.setXY((float) (Math.random() * world.getRoom().getWidth()), 16);
                    deadPlayerActor.repair();
                });
            }
        }
    }

}




















