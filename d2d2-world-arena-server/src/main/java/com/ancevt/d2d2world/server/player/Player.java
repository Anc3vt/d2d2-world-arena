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

import com.ancevt.net.connection.IConnection;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Player {

    private final int id;
    private final String name;
    private final String address;
    private final int color;
    private final String clientProtocolVersion;
    private int frags;
    private String ip;
    private String roomId;
    private IConnection connection;

    private int lastSeenChatMessageId;
    private int ping;

    private boolean rconLoggedIn;

    public Player(@NotNull IConnection connection,
                  int id,
                  @NotNull String name,
                  int color,
                  @NotNull String clientProtocolVersion) {

        this.connection = connection;
        this.name = name;
        this.id = id;
        this.address = connection.getRemoteAddress();
        this.color = color;
        this.clientProtocolVersion = clientProtocolVersion;
    }

    public IConnection getConnection() {
        return connection;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getIp() {
        if (ip == null) {
            String text = getAddress();
            if (text.startsWith("/")) {
                text = text.replaceAll("/", "");
            }
            String[] split = text.split(":");
            ip = split[0];
        }
        return ip;
    }

    public boolean isRconLoggedIn() {
        return rconLoggedIn;
    }

    public void setRconLoggedIn(boolean rconLoggedIn) {
        this.rconLoggedIn = rconLoggedIn;
    }

    public int getLastSeenChatMessageId() {
        return lastSeenChatMessageId;
    }

    public void setLastSeenChatMessageId(int lastSeenChatMessageId) {
        this.lastSeenChatMessageId = lastSeenChatMessageId;
    }

    public String getClientProtocolVersion() {
        return clientProtocolVersion;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public int getPingValue() {
        return ping;
    }

    public void setPingValue(int ping) {
        this.ping = ping;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", color=" + color +
                ", clientProtocolVersion='" + clientProtocolVersion + '\'' +
                ", lastSeenChatMessageId=" + lastSeenChatMessageId +
                ", ping=" + ping +
                ", rconLoggedIn=" + rconLoggedIn +
                ", roomId=" + roomId +
                '}';
    }

    public void incrementFrags() {
        setFrags(getFrags() + 1);
    }

    public void decrementFrags() {
        setFrags(getFrags() - 1);
    }
}
