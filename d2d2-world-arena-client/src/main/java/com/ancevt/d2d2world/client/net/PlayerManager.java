
package com.ancevt.d2d2world.client.net;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlayerManager {
    public static final PlayerManager PLAYER_MANAGER = new PlayerManager();

    private final Map<Integer, Player> playerMap;

    private PlayerManager() {
        playerMap = new HashMap<>();
    }

    public @NotNull Player addPlayer(int id, @NotNull String name, int color) {
        Player player = playerMap.get(id);
        if (player == null) {
            player = new Player(id, name, color);
            playerMap.put(id, player);
        }
        player.setColor(color);
        player.setName(name);
        return player;
    }

    public boolean hasPlayer(int playerId) {
        return playerMap.containsKey(playerId);
    }

    public @NotNull Optional<Player> getPlayerByPlayerActorGameObjectId(int gameObjectId) {
        return playerMap.values()
                .stream()
                .filter(p -> p.getPlayerActorGameObjectId() == gameObjectId)
                .findAny();
    }

    public @NotNull Optional<Player> getPlayerById(int playerId) {
        return Optional.ofNullable(playerMap.get(playerId));
    }

    public @NotNull Optional<Player> removePlayer(int remotePlayerId) {
        return Optional.ofNullable(playerMap.remove(remotePlayerId));
    }

    public @NotNull List<Player> getPlayerList() {
        return List.copyOf(playerMap.values());
    }

    public void clear() {
        playerMap.clear();
    }
}
