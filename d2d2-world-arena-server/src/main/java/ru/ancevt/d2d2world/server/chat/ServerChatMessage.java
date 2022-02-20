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
package ru.ancevt.d2d2world.server.chat;

import org.jetbrains.annotations.NotNull;

public class ServerChatMessage {

    public static final int DEFAULT_COLOR = 0xFFFFFF;

    private final String playerName;
    private final int id;
    private final String text;
    private final int playerId;
    private final int playerColor;
    private final int textColor;


    public ServerChatMessage(int id, @NotNull String text, int textColor) {
        this.id = id;
        this.text = text;
        this.playerId = 0;
        this.playerName = null;
        this.playerColor = 0;
        this.textColor = textColor;
    }

    public ServerChatMessage(int id,
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
