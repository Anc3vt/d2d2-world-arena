/*
 *   D2D2 World Arena Desktop
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
package ru.ancevt.d2d2world.game.ui.chat;

import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2world.game.ui.Font;
import ru.ancevt.d2d2world.game.ui.UiText;

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

    public ChatMessage(int id,
                       int playerId,
                       String playerName,
                       int playerColor,
                       String messageText) {

        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.text = messageText;
        nameUiText = new UiText();
        textUiText = new UiText();

        String playerNameToShow = format("%s(%d):", playerName, playerId);

        nameUiText.setColor(Color.of(playerColor));
        nameUiText.setText(playerNameToShow);
        nameUiText.setSize(playerNameToShow.length() * Font.getBitmapFont().getCharInfo('0').width() + 10, 30);

        textUiText.setText(messageText);
        textUiText.setX(nameUiText.getWidth());

        textUiText.setWidth(DEFAULT_WIDTH);
        textUiText.setHeight(DEFAULT_HEIGHT);

        nameUiText.setHeight(DEFAULT_HEIGHT);

        add(nameUiText);
        add(textUiText);
    }

    public ChatMessage(int id, String messageText) {
        this.id = id;
        this.playerId = 0;
        this.playerName = null;
        this.text = messageText;
        nameUiText = null;
        textUiText = new UiText();

        textUiText.setWidth(DEFAULT_WIDTH);
        textUiText.setHeight(DEFAULT_HEIGHT);

        textUiText.setText(messageText);
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
                '}';
    }
}
