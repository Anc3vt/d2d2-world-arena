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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ru.ancevt.d2d2world.server.ModuleContainer.modules;

public class ServerChat {
    private static final int MAX_MESSAGES = 1024;
    private static final int DELETE_MESSAGES = MAX_MESSAGES / 4;

    private static int idCounter;

    private final List<ChatMessage> messages;

    private final List<ServerChatListener> serverChatListeners;

    public ServerChat() {
        messages = new LinkedList<>();
        serverChatListeners = new ArrayList<>();
    }

    public void addChatListener(ServerChatListener l) {
        serverChatListeners.add(l);
    }

    public void removeChatListener(ServerChatListener l) {
        serverChatListeners.remove(l);
    }

    public int getNewChatMessageId() {
        if (idCounter == Integer.MAX_VALUE) {
            idCounter = 0;
        }
        return ++idCounter;
    }

    public void text(@NotNull String text) {
        ChatMessage chatMessage = new ChatMessage(getNewChatMessageId(), text);
        messages.add(chatMessage);
        serverChatListeners.forEach(l -> l.chatMessage(chatMessage));
        checkAndFixMessageListSize();
    }

    public void playerText(@NotNull String text, int playerId, String playerName, int playerColor) {
        ChatMessage chatMessage = new ChatMessage(getNewChatMessageId(), text, playerId, playerName, playerColor);
        messages.add(chatMessage);
        serverChatListeners.forEach(l -> l.chatMessage(chatMessage));
        checkAndFixMessageListSize();
    }

    private void checkAndFixMessageListSize() {
        if (messages.size() > MAX_MESSAGES) {
            int counter = DELETE_MESSAGES;
            while (counter-- > 0) {
                messages.remove(0);
            }
        }
    }

    public List<ChatMessage> getMessagesFromIdExcluding(int chatMessageId) {
        int fromIndex = -1;
        int indexCounter = 0;
        for (ChatMessage message : messages) {
            if (message.getId() > chatMessageId) {
                fromIndex = indexCounter;
                break;
            }
            indexCounter++;
        }
        if (fromIndex != -1) {
            return messages.subList(fromIndex, messages.size() - 1);
        }

        return List.of();
    }


}
