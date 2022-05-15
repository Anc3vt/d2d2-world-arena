
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
