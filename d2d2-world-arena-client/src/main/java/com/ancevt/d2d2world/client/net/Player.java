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

package com.ancevt.d2d2world.client.net;

public class Player {

    private final int id;
    private String name;
    private int color;
    private int ping;
    private int frags;

    private boolean chatOpened;
    private int playerActorGameObjectId;

    public Player(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
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
