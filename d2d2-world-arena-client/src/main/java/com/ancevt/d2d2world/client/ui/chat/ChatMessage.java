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
package com.ancevt.d2d2world.client.ui.chat;

import com.ancevt.d2d2.components.ComponentFont;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.text.BitmapText;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;

public class ChatMessage extends Container {

    public static final float DEFAULT_WIDTH = 1000;
    public static final float DEFAULT_HEIGHT = 16;

    private final int id;
    private final int playerId;
    private final String playerName;
    private final String text;
    private final BitmapText nameBitmapText;
    private final BitmapText bitmapText;
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
        nameBitmapText = new BitmapText();
        nameBitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddleGlow());
        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddleGlow());

        String playerNameToShow = format("%s(%d):", playerName, playerId);

        nameBitmapText.setColor(Color.of(playerColor));
        bitmapText.setColor(textColor);
        nameBitmapText.setText(playerNameToShow);
        nameBitmapText.setSize(playerNameToShow.length() * ComponentFont.getBitmapFontMiddle().getCharInfo('0').width() + 10, 30);

        bitmapText.setText(messageText);
        bitmapText.setX(nameBitmapText.getWidth());
        bitmapText.setWidth(DEFAULT_WIDTH);
        bitmapText.setHeight(DEFAULT_HEIGHT);

        nameBitmapText.setHeight(DEFAULT_HEIGHT);

        bitmapText.setVertexBleedingFix(0);
        nameBitmapText.setVertexBleedingFix(0);

        add(nameBitmapText);
        add(bitmapText);
    }

    public ChatMessage(int id, String messageText, Color textColor) {
        this.id = id;
        this.playerId = 0;
        this.playerName = null;
        this.text = messageText;
        this.textColor = textColor;
        nameBitmapText = null;
        bitmapText = new BitmapText();
        bitmapText.setBitmapFont(ComponentFont.getBitmapFontMiddleGlow());

        bitmapText.setWidth(DEFAULT_WIDTH);
        bitmapText.setHeight(DEFAULT_HEIGHT);
        bitmapText.setColor(textColor);
        bitmapText.setText(messageText);

        bitmapText.setVertexBleedingFix(0);

        add(bitmapText);
    }

    public boolean isFromPlayer() {
        return playerName != null;
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
                ", nameUiText=" + nameBitmapText +
                ", textUiText=" + bitmapText +
                ", textColor=" + textColor +
                '}';
    }
}

