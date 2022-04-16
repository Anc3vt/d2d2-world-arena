/*
 *   D2D2 World Arena Networking
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
package com.ancevt.d2d2world.net.client;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final int id;
    private String name;
    private int color;
    private int ping;
    private int frags;

    private boolean chatOpened;
    private int playerActorGameObjectId;

    private final List<Integer> pingValues;

    public Player(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
        pingValues = new ArrayList<>();
    }

    public void setPlayerActorGameObjectId(int playerActorGameObjectId) {
        this.playerActorGameObjectId = playerActorGameObjectId;
    }

    public int getPlayerActorGameObjectId() {
        return playerActorGameObjectId;
    }

    public void setChatOpened(boolean b) {
        this.chatOpened = b;
    }

    public boolean isChatOpened() {
        return chatOpened;
    }

    public int getFrags() {
        return frags;
    }

    public void incrementFrags() {
        setFrags(getFrags() + 1);
    }

    public void decrementFrags() {
        setFrags(getFrags() - 1);
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RemotePlayer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", ping=" + ping +
                ", frags=" + frags +
                '}';
    }

    public void update(String remotePlayerName, int remotePlayerColor) {
        setName(remotePlayerName);
        setColor(remotePlayerColor);
    }
}
