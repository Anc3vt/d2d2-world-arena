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
package ru.ancevt.d2d2world.desktop.scene;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.concurrent.Lock;
import ru.ancevt.commons.hash.MD5;
import ru.ancevt.d2d2.debug.FpsMeter;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.input.KeyCode;
import ru.ancevt.d2d2world.desktop.ClientCommandProcessor;
import ru.ancevt.d2d2world.desktop.DesktopConfig;
import ru.ancevt.d2d2world.desktop.ui.TabWindow;
import ru.ancevt.d2d2world.desktop.ui.UiTextInputProcessor;
import ru.ancevt.d2d2world.desktop.ui.chat.Chat;
import ru.ancevt.d2d2world.desktop.ui.chat.ChatEvent;
import ru.ancevt.d2d2world.net.client.Client;
import ru.ancevt.d2d2world.net.client.ClientListener;
import ru.ancevt.d2d2world.net.client.RemotePlayer;
import ru.ancevt.d2d2world.net.client.RemotePlayerManager;
import ru.ancevt.d2d2world.net.client.ServerInfo;
import ru.ancevt.d2d2world.net.transfer.FileReceiver;
import ru.ancevt.d2d2world.net.transfer.FileReceiverManager;
import ru.ancevt.net.tcpb254.CloseStatus;

import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static ru.ancevt.d2d2world.desktop.ModuleContainer.modules;

@Slf4j
public class GameRoot extends Root implements ClientListener, FileReceiverManager.FileReceiverManagerListener {

    public static final int DEFAULT_PORT = 2245;

    // TODO: refactor
    public static GameRoot INSTANCE;

    private final Client client = modules.get(Client.class);
    private final DesktopConfig desktopConfig = modules.get(DesktopConfig.class);
    private final Chat chat = modules.get(Chat.class);
    private String server;
    private final WorldScene worldScene;
    private final ClientCommandProcessor clientCommandProcessor = modules.get(ClientCommandProcessor.class);
    private final TabWindow tabWindow;
    private String serverName;

    public GameRoot() {
        UiTextInputProcessor.enableRoot(this);

        setBackgroundColor(Color.DARK_BLUE);
        addEventListener(Event.ADD_TO_STAGE, this::addToStage);

        client.addClientListener(this);

        chat.addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            var e = (ChatEvent) event;
            String text = e.getText();
            if (text.startsWith("/") || text.startsWith("\\")) {
                // if typed command is one of registered client command then don't send that command text to the server
                text = text.replace('\\', '/');
                if (clientCommand(text)) return;
            }
            if (client.isConnected()) {
                client.sendChatMessage(text);
            }
        });

        addEventListener(InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.PAGE_UP -> chat.setScroll(chat.getScroll() - 10);
                case KeyCode.PAGE_DOWN -> chat.setScroll(chat.getScroll() + 10);
                case KeyCode.F8 -> chat.setShadowEnabled(!chat.getShadowEnabled());
                case KeyCode.F6 -> {
                    if (!chat.isInputOpened()) {
                        chat.openInput();
                    } else {
                        chat.closeInput();
                    }
                }
                case KeyCode.TAB -> {
                    chat.setVisible(false);
                    setTabWindowVisible(true);
                }
            }
        });

        addEventListener(InputEvent.KEY_UP, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.TAB -> {
                    chat.setVisible(true);
                    setTabWindowVisible(false);
                }
            }
        });

        worldScene = new WorldScene();
        add(worldScene);

        add(chat, 10, 10);

        tabWindow = new TabWindow();

        FileReceiverManager.INSTANCE.addFileReceiverManagerListener(this);

        INSTANCE = this;
    }

    private void setTabWindowVisible(boolean value) {
        tabWindow.removeFromParent();

        if (value) {
            tabWindow.setServerName(serverName, 0, 0);
            tabWindow.setPlayers(
                    client.getLocalPlayerId(),
                    client.getLocalPlayerName(),
                    client.getLocalPlayerFrags(),
                    client.getLocalPlayerPing(),
                    Color.of(client.getLocalPlayerColor()),
                    RemotePlayerManager.INSTANCE.getRemotePlayerList()
            );

            add(tabWindow);
        }
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void remotePlayerIntroduce(@NotNull RemotePlayer remotePlayer) {
        worldScene.addRemotePlayer(remotePlayer);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void remotePlayerEnterServer(int remotePlayerId, @NotNull String remotePlayerName, int remotePlayerColor) {

    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverInfo(@NotNull ServerInfo result) {
        tabWindow.setServerName(result.getName(), result.getPlayers().size(), result.getMaxPlayers());
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverTextToPlayer(@NotNull String text, int textColor) {
        chat.addMessage(text, Color.of(textColor));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void fileData(@NotNull String headers, byte[] fileData) {

    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void rconResponse(String rconResponseData) {
        rconResponseData.lines().forEach(chat::addMessage);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void remotePlayerExit(@NotNull RemotePlayer remotePlayer) {
        chat.addMessage(remotePlayer.getName() + "(" + remotePlayer.getId() + ") exit", Color.GRAY);
        worldScene.removeRemotePlayer(remotePlayer);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerEnterServer(int localPlayerId, int localPlayerColor, @NotNull String serverProtocolVersion) {
        chat.addMessage("Your id is " + localPlayerId + ", server protocol version is " + serverProtocolVersion
                , Color.WHITE);
        worldScene.start();

        String rconPassword = desktopConfig.getString(DesktopConfig.RCON_PASSWORD);
        client.sendRconLoginRequest(MD5.hash(rconPassword));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverChat(int chatMessageId, @NotNull String chatMessageText, int chatMessageTextColor) {
        chat.addMessage("Server: " + chatMessageText, Color.of(chatMessageTextColor));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerChat(int chatMessageId,
                           int playerId,
                           @NotNull String playerName,
                           int playerColor,
                           @NotNull String chatMessageText,
                           int textColor) {

        chat.addPlayerMessage(chatMessageId, playerId, playerName, playerColor, chatMessageText, Color.of(textColor));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionClosed(@NotNull CloseStatus status) {
        worldScene.stop();
        chat.addMessage(status.getErrorMessage(), Color.RED);
        new Lock().lock(1, TimeUnit.SECONDS);
        start(server, client.getLocalPlayerName());
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionEstablished() {
        chat.addMessage("Connection established", Color.GREEN);
        client.sendPlayerEnterRequest();
    }

    /**
     * {@link FileReceiverManager.FileReceiverManagerListener} method
     */
    @Override
    public void progress(@NotNull FileReceiver fileReceiver) {
        int proc = (fileReceiver.bytesLoaded() / fileReceiver.bytesTotal()) * 100;
        chat.addMessage(
                format("%d%% sync %s", proc, fileReceiver.getPath()),
                Color.DARK_GRAY
        );
    }

    /**
     * {@link FileReceiverManager.FileReceiverManagerListener} method
     */
    @Override
    public void complete(@NotNull FileReceiver fileReceiver) {
        chat.addMessage(
                format("sync complete %s", fileReceiver.getPath()),
                Color.DARK_GRAY
        );
    }

    private boolean clientCommand(String text) {
        return clientCommandProcessor.process(text);
    }

    private void addToStage(Event event) {
        add(new FpsMeter(), 0, getStage().getStageWidth() - 100);
    }

    public void start(@NotNull String server, String localPlayerName) {
        log.debug("Staring... server: {}, player name: {}", server, localPlayerName);
        if (client.isConnected()) {
            client.close();
        }
        worldScene.init();

        this.server = server;

        String host = server.contains(":") ? server.split(":")[0] : server;
        int port = server.contains(":") ? Integer.parseInt(server.split(":")[1]) : DEFAULT_PORT;

        client.setLocalPlayerName(localPlayerName);

        chat.addMessage("Connecting to " + server + "...", Color.GRAY);
        client.connect(host, port);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void exit() {
        chat.dispose();
        System.exit(0);
    }

}



























