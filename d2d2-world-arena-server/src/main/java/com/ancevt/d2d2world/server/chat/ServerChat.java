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
