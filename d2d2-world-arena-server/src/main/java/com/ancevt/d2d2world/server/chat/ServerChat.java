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
package com.ancevt.d2d2world.server.chat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ServerChat {
    public static final ServerChat MODULE_CHAT = new ServerChat();

    private static final int MAX_MESSAGES = 256;
    private static final int DELETE_MESSAGES = MAX_MESSAGES / 4;
    private static final int DEFAULT_PLAYER_TEXT_COLOR = 0xFFFFFF;
    private static final int DEFAULT_TEXT_COLOR = 0xBBBBAA;

    private static int idCounter;

    private final List<ChatMessage> messages;

    private final List<ServerChatListener> serverChatListeners;

    private ServerChat() {
        messages = new LinkedList<>();
        serverChatListeners = new ArrayList<>();
    }

    public void addServerChatListener(ServerChatListener l) {
        serverChatListeners.add(l);
    }

    public void removeServerCharListener(ServerChatListener l) {
        serverChatListeners.remove(l);
    }

    public int getNewChatMessageId() {
        if (idCounter == Integer.MAX_VALUE) {
            idCounter = 0;
        }
        return ++idCounter;
    }

    public void text(@NotNull String text) {
        text(text, DEFAULT_TEXT_COLOR);
    }

    public void text(@NotNull String text, int textColor) {
        ChatMessage serverChatMessage = new ChatMessage(getNewChatMessageId(), text, textColor);
        messages.add(serverChatMessage);
        serverChatListeners.forEach(l -> l.chatMessage(serverChatMessage));
        checkAndFixMessageListSize();
    }

    public void playerText(@NotNull String text, int playerId, String playerName, int playerColor) {
        playerText(text, playerId, playerName, playerColor, DEFAULT_PLAYER_TEXT_COLOR);
    }

    public void playerText(@NotNull String text, int playerId, String playerName, int playerColor, int textColor) {

        ChatMessage serverChatMessage = new ChatMessage(
                getNewChatMessageId(), text, playerId, playerName, playerColor, textColor);

        messages.add(serverChatMessage);
        serverChatListeners.forEach(l -> l.chatMessage(serverChatMessage));
        checkAndFixMessageListSize();
    }

    private void checkAndFixMessageListSize() {
        if (messages.size() > MAX_MESSAGES) {
            int counter = DELETE_MESSAGES;
            while (counter-- > 0) {
                messages.retainAll(messages.subList(0, DELETE_MESSAGES));
            }
        }
    }

    public List<ChatMessage> getMessages(int count) {
        if(messages.size() > count) {
            return messages.subList(messages.size() - 1 - count, messages.size());
        }
        return List.copyOf(messages);
    }


}
