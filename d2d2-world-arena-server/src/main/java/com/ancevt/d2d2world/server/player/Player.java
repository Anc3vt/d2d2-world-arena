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
