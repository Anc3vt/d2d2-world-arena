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
