/*
 *   D2D2 World Arena Client
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
package com.ancevt.d2d2world.client.ui.chat;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2world.client.ui.Font;
import com.ancevt.d2d2world.client.ui.UiText;

import static java.lang.String.format;

public class ChatMessage extends DisplayObjectContainer {

    public static final float DEFAULT_WIDTH = 1000;
    public static final float DEFAULT_HEIGHT = 16;

    private final int id;
    private final int playerId;
    private final String playerName;
    private final String text;
    private final UiText nameUiText;
    private final UiText textUiText;
    private final Color textColor;

    public ChatMessage(int id,
                       int playerId,
                       @NotNull String playerName,
                       int playerColor,
                       @NotNull String messageText,
                       Color textColor) {

        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.text = messageText;
        this.textColor = textColor;
        nameUiText = new UiText();
        textUiText = new UiText();

        String playerNameToShow = format("%s(%d):", playerName, playerId);

        nameUiText.setColor(Color.of(playerColor));
        textUiText.setColor(textColor);
        nameUiText.setText(playerNameToShow);
        nameUiText.setSize(playerNameToShow.length() * Font.getBitmapFont().getCharInfo('0').width() + 10, 30);

        textUiText.setText(messageText);
        textUiText.setX(nameUiText.getWidth());
        textUiText.setWidth(DEFAULT_WIDTH);
        textUiText.setHeight(DEFAULT_HEIGHT);

        nameUiText.setHeight(DEFAULT_HEIGHT);

        textUiText.setVertexBleedingFix(0);
        nameUiText.setVertexBleedingFix(0);

        add(nameUiText);
        add(textUiText);
    }

    public ChatMessage(int id, String messageText, Color textColor) {
        this.id = id;
        this.playerId = 0;
        this.playerName = null;
        this.text = messageText;
        this.textColor = textColor;
        nameUiText = null;
        textUiText = new UiText();

        textUiText.setWidth(DEFAULT_WIDTH);
        textUiText.setHeight(DEFAULT_HEIGHT);
        textUiText.setColor(textColor);
        textUiText.setText(messageText);

        textUiText.setVertexBleedingFix(0);

        add(textUiText);
    }

    public void setShadowEnabled(boolean b) {
        if (nameUiText != null) {
            nameUiText.setShadowEnabled(b);
        }
        textUiText.setShadowEnabled(b);
    }

    public boolean isFromPlayer() {
        return playerName != null;
    }

    public boolean isShadowEnabled() {
        return textUiText.isShadowEnabled();
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Color getTextColor() {
        return textColor;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", text='" + text + '\'' +
                ", nameUiText=" + nameUiText +
                ", textUiText=" + textUiText +
                ", textColor=" + textColor +
                '}';
    }
}

