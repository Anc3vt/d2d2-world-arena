/*
 *   D2D2 World Arena Client
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
