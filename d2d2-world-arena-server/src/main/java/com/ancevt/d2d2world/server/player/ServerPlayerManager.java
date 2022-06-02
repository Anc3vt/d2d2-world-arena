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

package com.ancevt.d2d2world.server.player;

import com.ancevt.d2d2.display.Color;
import com.ancevt.net.connection.IConnection;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Slf4j
public class ServerPlayerManager {

    public static final ServerPlayerManager PLAYER_MANAGER = new ServerPlayerManager();

    private final Map<Integer, Player> playerMap;
    private final List<Player> playerList;

    private ServerPlayerManager() {
        playerMap = new HashMap<>();
        playerList = new ArrayList<>();
    }

    public Player createPlayer(IConnection connection,
                               int playerId,
                               String playerName,
                               String clientProtocolVersion) {

        return createPlayer(
                new Player(
                        connection,
                        playerId,
                        playerName,
                        generateColor(),
                        clientProtocolVersion
                )
        );
    }

    private static int generateColor() {
        return Color.createVisibleRandomColor().getValue();
    }

    @Contract("_ -> param1")
    private @NotNull Player createPlayer(Player player) {
        playerMap.put(player.getId(), player);
        playerList.add(player);
        log.info("Add player: {}({}), ip: {}", player.getName(), player.getId(), player.getAddress());
        return player;
    }

    public void removePlayer(int playerId) {
        getPlayerById(playerId).ifPresent(this::removePlayer);
    }

    public void removePlayer(@NotNull Player player) {
        playerMap.remove(player.getId());
        playerList.remove(player);
        log.info("Remove player: {}({}), ip: {}", player.getName(), player.getId(), player.getAddress());
    }

    public Optional<Player> getPlayerById(int id) {
        return Optional.ofNullable(playerMap.get(id));
    }

    public Optional<Player> getPlayerByIndex(int index) {
        return Optional.ofNullable(playerList.get(index));
    }

    public List<Player> getPlayerList() {
        return List.copyOf(playerList);
    }

    public boolean isEmpty() {
        return playerList.isEmpty();
    }

    public int getPlayerCount() {
        return playerList.size();
    }

    public boolean containsPlayer(int playerId) {
        return getPlayerById(playerId).isPresent();
    }

    public List<Player> getPlayerListInRoom(@NotNull String roomId) {
        return playerList.stream()
                .filter(player -> roomId.equals(player.getRoomId()))
                .toList();
    }
}
