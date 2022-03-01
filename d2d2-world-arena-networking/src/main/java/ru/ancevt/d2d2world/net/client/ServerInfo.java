/*
 *   D2D2 World Arena Desktop
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

import java.util.List;

public class ServerInfo {
    private final String name;
    private final String version;
    private final String protocolVersion;
    private final String map;
    private final String mapKit;
    private final String mod;
    private final List<RemotePlayer> players;

    public ServerInfo(@NotNull String name,
                      @NotNull String version,
                      @NotNull String protocolVersion,
                      @NotNull String map,
                      @NotNull String mapKit,
                      @NotNull String mod,
                      int maxPlayers,
                      @NotNull List<RemotePlayer> players) {
        this.name = name;
        this.version = version;
        this.protocolVersion = protocolVersion;
        this.map = map;
        this.mapKit = mapKit;
        this.mod = mod;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getMapKit() {
        return mapKit;
    }

    public String getMap() {
        return map;
    }

    public String getMod() {
        return mod;
    }

    public List<RemotePlayer> getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return 0;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", map='" + map + '\'' +
                ", mapKit='" + mapKit + '\'' +
                ", mod='" + mod + '\'' +
                ", players=" + players +
                '}';
    }
}
