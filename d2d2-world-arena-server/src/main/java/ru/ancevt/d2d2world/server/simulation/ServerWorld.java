package ru.ancevt.d2d2world.server.simulation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.concurrent.Async;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.starter.norender.NoRenderStarter;
import ru.ancevt.d2d2world.gameobject.PlayerActor;
import ru.ancevt.d2d2world.map.GameMap;
import ru.ancevt.d2d2world.map.MapIO;
import ru.ancevt.d2d2world.mapkit.CharacterMapkit;
import ru.ancevt.d2d2world.mapkit.MapkitItem;
import ru.ancevt.d2d2world.mapkit.MapkitManager;
import ru.ancevt.d2d2world.net.protocol.SyncDataAggregator;
import ru.ancevt.d2d2world.server.content.ServerContentManager;
import ru.ancevt.d2d2world.server.player.Player;
import ru.ancevt.d2d2world.sync.ISyncDataAggregator;
import ru.ancevt.d2d2world.world.World;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.ancevt.d2d2world.data.Properties.setProperties;
import static ru.ancevt.d2d2world.server.content.ServerContentManager.MODULE_CONTENT_MANAGER;
import static ru.ancevt.d2d2world.server.service.ServerSender.MODULE_SENDER;

@Slf4j
public class ServerWorld {

    public static final ServerWorld MODULE_WORLD = new ServerWorld();

    private World world;
    private final ISyncDataAggregator syncDataAggregator;

    private final Map<Integer, PlayerActor> playerActorMap = new ConcurrentHashMap<>();

    private ServerWorld() {
        syncDataAggregator = new SyncDataAggregator();
    }

    public void start() {
        Root root = D2D2.init(new NoRenderStarter(900, 600));

        world = new World(syncDataAggregator);
        root.add(world);
        Async.run(D2D2::loop);
    }

    public World getWorld() {
        return world;
    }

    public void loadMap(String mapName) {
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

    private void this_eachFrame(Event event) {
        if (syncDataAggregator.hasData()) {
            MODULE_SENDER.sendToAll(syncDataAggregator.createSyncMessage());
        }
    }

    public void playerController(int playerId, int controllerState) {
        System.out.println("playerController " + playerId + " " + controllerState);
        PlayerActor playerActor = playerActorMap.get(playerId);
        if (playerActor != null) {
            playerActor.getController().applyState(controllerState);
        }
    }

    public PlayerActor getPlayerActor(int playerId) {
        return playerActorMap.get(playerId);
    }

    public void addPlayer(@NotNull Player player) {
        MapkitItem mapkitItem = MapkitManager.getInstance().getByName(CharacterMapkit.NAME).getItem("character_blake");
        PlayerActor playerActor = (PlayerActor) mapkitItem.createGameObject(world.getMap().getNextFreeGameObjectId());
        setProperties(playerActor, mapkitItem.getDataEntry());
        playerActor.getController().setEnabled(true);
        playerActor.setXY(64, 64);
        world.addGameObject(playerActor, 5, false);
        playerActorMap.put(player.getId(), playerActor);

        log.info("Add player actor {}", playerActor);
    }

    public void removePlayer(@NotNull Player player) {
        PlayerActor playerActor = playerActorMap.remove(player.getId());
        if (playerActor != null) {
            world.removeGameObject(playerActor, false);
        }

        log.info("Remove player actor {}", playerActor);
    }
}




















