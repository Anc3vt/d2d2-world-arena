/*
 *   D2D2 World Desktop
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
package ru.ancevt.d2d2world.game.scene;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.concurrent.Lock;
import ru.ancevt.d2d2.debug.FpsMeter;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.input.KeyCode;
import ru.ancevt.d2d2world.game.ui.TextInputProcessor;
import ru.ancevt.d2d2world.game.ui.chat.Chat;
import ru.ancevt.d2d2world.game.ui.chat.ChatEvent;
import ru.ancevt.d2d2world.net.client.Client;
import ru.ancevt.d2d2world.net.client.ClientListener;
import ru.ancevt.d2d2world.net.client.RemotePlayer;
import ru.ancevt.net.messaging.CloseStatus;

import java.util.concurrent.TimeUnit;

public class GameRoot extends Root implements ClientListener {

    public static final int DEFAULT_PORT = 2245;

    private final Chat chat;
    private final Client client;
    private String server;
    private WorldScene worldScene;

    public GameRoot() {
        TextInputProcessor.enableRoot(this);

        setBackgroundColor(Color.DARK_BLUE);
        addEventListener(Event.ADD_TO_STAGE, this::addToStage);
        chat = new Chat(0, null);

        client = new Client();
        client.addClientListener(this);

        chat.addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            if(client.isConnected()) {
                var e = (ChatEvent) event;
                client.sendChatMessage(e.getText());
            }
        });

        addEventListener(InputEvent.KEY_DOWN, e -> {
            var event = (InputEvent) e;
            switch (event.getKeyCode()) {
                case KeyCode.PAGE_UP -> {
                    chat.setScroll(chat.getScroll() - 10);
                }

                case KeyCode.PAGE_DOWN -> {
                    chat.setScroll(chat.getScroll() + 10);
                }

                case KeyCode.F8 -> {
                    chat.setShadowEnabled(!chat.getShadowEnabled());
                }

                case KeyCode.F6 -> {
                    if (!chat.isInputOpened()) chat.openInput();
                }
            }
        });

        worldScene = new WorldScene(client, chat);
        add(worldScene);

        add(chat, 10, 10);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void remotePlayerIntroduce(@NotNull RemotePlayer remotePlayer) {
        //chat.addMessage(0, "There are Player " + remotePlayer.getName() + "(" + remotePlayer.getId() + ")");

        worldScene.addRemotePlayer(remotePlayer);
    }

    @Override
    public void remotePlayerEnterServer(int remotePlayerId, String remotePlayerName, int remotePlayerColor) {

    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void remotePlayerExit(@NotNull RemotePlayer remotePlayer) {
        // chat.addMessage(0, remotePlayer.getName() + "(" + remotePlayer.getId() + ") exit");
        worldScene.removeRemotePlayer(remotePlayer);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerEnterServer(int localPlayerId, int localPlayerColor, @NotNull String serverProtocolVersion) {
        chat.setLocalPlayerId(localPlayerId);
        chat.addMessage(0, "Your id is " + localPlayerId + ", server protocol version is " + serverProtocolVersion);
        worldScene.start();
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverChat(int chatMessageId, @NotNull String chatMessageText) {
        chat.addMessage(chatMessageId, "Server: " + chatMessageText);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerChat(int chatMessageId, int playerId, @NotNull String playerName, int playerColor, @NotNull String chatMessageText) {
        chat.addMessage(chatMessageId, playerId, playerName, playerColor, chatMessageText);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionClosed(CloseStatus status) {
        worldScene.stop();
        chat.addMessage(0, status.getErrorMessage());
        new Lock().lock(5, TimeUnit.SECONDS);
        start(server, client.getLocalPlayerName());
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionEstablished() {
        chat.addMessage(0, "Connection established");
        client.sendPlayerEnterRequest();
    }

    private void addToStage(Event event) {
        add(new FpsMeter(), 0, getStage().getStageWidth() - 100);
    }

    public void start(String server, String localPlayerName) {
        worldScene.init();

        this.server = server;

        String host = server.contains(":") ? server.split(":")[0] : server;
        int port = server.contains(":") ? Integer.parseInt(server.split(":")[1]) : DEFAULT_PORT;

        client.setLocalPlayerName(localPlayerName);

        chat.addMessage(0, "Connecting to " + server + "...");
        new Lock().lock(100, TimeUnit.MILLISECONDS);
        client.connect(host, port);

        chat.setLocalPlayerName(localPlayerName);
    }


}



























