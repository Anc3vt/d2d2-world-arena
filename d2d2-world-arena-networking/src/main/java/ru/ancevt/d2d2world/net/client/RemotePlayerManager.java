/*
 *   D2D2 World Arena Networking
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
package ru.ancevt.d2d2world.net.client;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RemotePlayerManager {
    public static final RemotePlayerManager PLAYER_MANAGER = new RemotePlayerManager();

    private final Map<Integer, RemotePlayer> remotePlayerMap;

    private RemotePlayerManager() {
        remotePlayerMap = new HashMap<>();
    }

    public @NotNull RemotePlayer createRemotePlayer(int id, @NotNull String name, int color) {
        RemotePlayer remotePlayer = new RemotePlayer(id, name, color);
        remotePlayerMap.put(id, remotePlayer);
        return remotePlayer;
    }

    public boolean hasRemotePlayer(int remotePlayerId) {
        return remotePlayerMap.containsKey(remotePlayerId);
    }

    public @NotNull Optional<RemotePlayer> getRemotePlayer(int remotePlayerId) {
        return Optional.ofNullable(remotePlayerMap.get(remotePlayerId));
    }

    public @NotNull Optional<RemotePlayer> removeRemotePlayer(int remotePlayerId) {
        return Optional.ofNullable(remotePlayerMap.remove(remotePlayerId));
    }

    public @NotNull List<RemotePlayer> getRemotePlayerList() {
        return List.copyOf(remotePlayerMap.values());
    }

    public void clear() {
        remotePlayerMap.clear();
    }
}
