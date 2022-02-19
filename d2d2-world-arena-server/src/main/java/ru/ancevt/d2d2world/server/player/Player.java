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
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Player {

    private final int id;
    private final String name;
    private final String address;
    private final int color;
    private final String clientProtocolVersion;
    private String ip;
    private int controllerState;
    private float x;
    private float y;

    private int lastSeenChatMessageId;
    private int ping;

    private boolean rconLoggedIn;

    public Player(int id,
                  @NotNull String name,
                  int color,
                  @NotNull String address,
                  @NotNull String clientProtocolVersion) {

        this.name = name;
        this.id = id;
        this.address = address;
        this.color = color;
        this.clientProtocolVersion = clientProtocolVersion;
    }

    public String getIp() {
        if(ip == null) {
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

    public void setControllerState(int controllerState) {
        this.controllerState = controllerState;
    }

    public int getControllerState() {
        return controllerState;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
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

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public int getPingValue() {
        return ping;
    }

    public void setPingValue(int ping) {
        this.ping = ping;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", color=" + color +
                ", clientProtocolVersion='" + clientProtocolVersion + '\'' +
                ", controllerState=" + controllerState +
                ", x=" + x +
                ", y=" + y +
                ", lastSeenChatMessageId=" + lastSeenChatMessageId +
                ", ping=" + ping +
                ", rconLoggedIn=" + rconLoggedIn +
                '}';
    }
}
