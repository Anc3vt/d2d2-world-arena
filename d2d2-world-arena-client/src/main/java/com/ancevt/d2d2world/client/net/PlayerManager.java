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
