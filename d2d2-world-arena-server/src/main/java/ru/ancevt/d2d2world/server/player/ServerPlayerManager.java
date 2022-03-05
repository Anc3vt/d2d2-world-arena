/*
 *   D2D2 World Arena Server
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
package ru.ancevt.d2d2world.server.player;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ServerPlayerManager {

    public static final ServerPlayerManager MODULE_PLAYER_MANAGER = new ServerPlayerManager();

    private final Map<Integer, Player> playerMap;
    private final List<Player> playerList;

    private ServerPlayerManager() {
        playerMap = new HashMap<>();
        playerList = new ArrayList<>();
    }

    public Player createPlayer(int playerId,
                               String playerName,
                               String clientProtocolVersion,
                               String address,
                               String extraData) {

        return createPlayer(
                new Player(
                        playerId,
                        playerName,
                        generateColor(),
                        address,
                        clientProtocolVersion
                )
        );
    }

    private static int generateColor() {
        return (int) (Math.random() * 0xFFFFFF);
    }

    private Player createPlayer(Player player) {
        playerMap.put(player.getId(), player);
        playerList.add(player);
        log.info("Add player: {}({}), ip: {}", player.getName(), player.getId(), player.getAddress());
        return player;
    }

    public void removePlayer(int playerId) {
        getPlayerById(playerId).ifPresent(this::removePlayer);
    }

    public void removePlayer(Player player) {
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

    public int getPlayerCount() {
        return playerList.size();
    }

    public boolean containsPlayer(int playerId) {
        return getPlayerById(playerId).isPresent();
    }
}
