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
package com.ancevt.d2d2world.server.chat;

import org.jetbrains.annotations.NotNull;

public class ChatMessage {

    public static final int DEFAULT_COLOR = 0xFFFFFF;

    private final String playerName;
    private final int id;
    private final String text;
    private final int playerId;
    private final int playerColor;
    private final int textColor;


    public ChatMessage(int id, @NotNull String text, int textColor) {
        this.id = id;
        this.text = text;
        this.playerId = 0;
        this.playerName = null;
        this.playerColor = 0;
        this.textColor = textColor;
    }

    public ChatMessage(int id,
                       @NotNull String text,
                       int playerId,
                       @NotNull String playerName,
                       int playerColor,
                       int textColor) {

        this.id = id;
        this.text = text;
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerColor = playerColor;
        this.textColor = textColor;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getPlayerColor() {
        return playerColor;
    }

    public boolean isFromPlayer() {
        return getPlayerName() != null;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public String toString() {
        return "ServerChatMessage{" +
                "playerName='" + playerName + '\'' +
                ", id=" + id +
                ", text='" + text + '\'' +
                ", playerId=" + playerId +
                ", playerColor=" + Integer.toString(playerColor, 16) +
                ", textColor=" + Integer.toString(textColor, 16) +
                '}';
    }
}
