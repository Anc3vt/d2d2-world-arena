/*
 *   D2D2 World Arena Server
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
package com.ancevt.d2d2world.server;

import java.time.LocalDateTime;

public class ServerState {

    public static final ServerState MODULE_SERVER_STATE = new ServerState();

    private String name;
    private String version;
    private String mapKit;
    private String map;
    private String mod;
    private int maxPlayers;
    private LocalDateTime startTime;

    private ServerState() {
        startTime = LocalDateTime.now();
        name = "";
        version = "";
        mapKit = "";
        map = "";
        mod = "";
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setMapKit(String mapKit) {
        this.mapKit = mapKit;
    }

    public String getMapKit() {
        return mapKit;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public String toString() {
        return "ServerStateInfo{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", mapKit='" + mapKit + '\'' +
                ", map='" + map + '\'' +
                ", mod='" + mod + '\'' +
                '}';
    }
}
