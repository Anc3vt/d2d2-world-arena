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
import ru.ancevt.d2d2world.debug.DebugPanel;
import ru.ancevt.d2d2world.desktop.DesktopConfig;
import ru.ancevt.d2d2world.desktop.ui.TabWindow;
import ru.ancevt.d2d2world.desktop.ui.UiTextInputProcessor;
import ru.ancevt.d2d2world.desktop.ui.chat.ChatEvent;
import ru.ancevt.d2d2world.net.client.ClientListener;
import ru.ancevt.d2d2world.net.client.Player;
import ru.ancevt.d2d2world.net.client.PlayerManager;
import ru.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import ru.ancevt.d2d2world.net.transfer.FileReceiver;
import ru.ancevt.d2d2world.net.transfer.FileReceiverManager;
import ru.ancevt.net.tcpb254.CloseStatus;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.ancevt.d2d2world.desktop.ClientCommandProcessor.MODULE_COMMAND_PROCESSOR;
import static ru.ancevt.d2d2world.desktop.DesktopConfig.MODULE_CONFIG;
import static ru.ancevt.d2d2world.desktop.ui.chat.Chat.MODULE_CHAT;
import static ru.ancevt.d2d2world.net.client.Client.MODULE_CLIENT;

@Slf4j
public class GameRoot extends Root implements ClientListener, FileReceiverManager.FileReceiverManagerListener {

    public static final int DEFAULT_PORT = 2245;

    // TODO: refactor
    public static GameRoot INSTANCE;

    private String server;
    private final WorldScene worldScene;
    private final TabWindow tabWindow;
    private String serverName;
    private int attempts;

    public GameRoot() {
        UiTextInputProcessor.enableRoot(this);

        setBackgroundColor(Color.BLACK);
        addEventListener(Event.ADD_TO_STAGE, this::addToStage);

        MODULE_CLIENT.addClientListener(this);

        MODULE_CHAT.addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            var e = (ChatEvent) event;
            String text = e.getText();
            if (text.startsWith("/") || text.startsWith("\\")) {
                // if typed command is one of registered client command then don't send that command text to the server
                text = text.replace('\\', '/');
                if (clientCommand(text)) return;
            }
            if (MODULE_CLIENT.isConnected()) {
                MODULE_CLIENT.sendChatMessage(text);
            }
        });

        addEventListener(InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.PAGE_UP -> MODULE_CHAT.setScroll(MODULE_CHAT.getScroll() - 10);
                case KeyCode.PAGE_DOWN -> MODULE_CHAT.setScroll(MODULE_CHAT.getScroll() + 10);
                case KeyCode.F8 -> MODULE_CHAT.setShadowEnabled(!MODULE_CHAT.isShadowEnabled());
                case KeyCode.F6 -> {
                    if (!MODULE_CHAT.isInputOpened()) {
                        MODULE_CHAT.openInput();
                    } else {
                        MODULE_CHAT.closeInput();
                    }
                }
                case KeyCode.TAB -> {
                    MODULE_CHAT.setVisible(false);
                    setTabWindowVisible(true);
                }
            }
        });

        addEventListener(InputEvent.KEY_UP, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.TAB -> {
                    MODULE_CHAT.setVisible(true);
                    setTabWindowVisible(false);
                }
            }
        });

        worldScene = new WorldScene();
        add(worldScene);

        add(MODULE_CHAT, 10, 10);

        tabWindow = new TabWindow();

        FileReceiverManager.INSTANCE.addFileReceiverManagerListener(this);

        INSTANCE = this;
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void remotePlayerIntroduce(@NotNull Player remotePlayer) {

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
    public void serverInfo(@NotNull ServerInfoDto result) {
        tabWindow.setServerName(result.getName(), result.getPlayers().size(), result.getMaxPlayers());
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverTextToPlayer(@NotNull String text, int textColor) {
        MODULE_CHAT.addMessage(text, Color.of(textColor));
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
    public void rconResponse(@NotNull String rconResponseData) {
        rconResponseData.lines().forEach(MODULE_CHAT::addMessage);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void remotePlayerExit(@NotNull Player remotePlayer) {

    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerEnterServer(int localPlayerId, int localPlayerColor, @NotNull String serverProtocolVersion) {
        MODULE_CHAT.addMessage("Your id is " + localPlayerId + ", server protocol version is " + serverProtocolVersion
                , Color.WHITE);
        worldScene.start();

        String rconPassword = MODULE_CONFIG.getString(DesktopConfig.RCON_PASSWORD);
        MODULE_CLIENT.sendRconLoginRequest(MD5.hash(rconPassword));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverChat(int chatMessageId, @NotNull String chatMessageText, int chatMessageTextColor) {
        MODULE_CHAT.addMessage("Server: " + chatMessageText, Color.of(chatMessageTextColor));
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

        MODULE_CHAT.addPlayerMessage(
                chatMessageId, playerId, playerName, playerColor, chatMessageText, Color.of(textColor));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionClosed(@NotNull CloseStatus status) {
        worldScene.stop();
        MODULE_CHAT.addMessage(status.getErrorMessage(), Color.RED);
        new Lock().lock(5, SECONDS);
        if (attempts < 10) {
            start(server, MODULE_CLIENT.getLocalPlayerName());
        } else {
            MODULE_CHAT.addMessage("Can't establish connection. Please try again later");
            setBackgroundColor(Color.BLACK);
        }
        attempts++;
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionEstablished() {
        MODULE_CHAT.addMessage("Connection established", Color.GREEN);
        MODULE_CLIENT.sendPlayerEnterRequest();
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void mapContentLoaded(String mapFilename) {
        worldScene.loadMap(mapFilename);
    }

    @Override
    public void localPlayerActorGameObjectId(int playerActorGameObjectId) {
        worldScene.setLocalPlayerActorGameObjectId(playerActorGameObjectId);
    }

    /**
     * {@link FileReceiverManager.FileReceiverManagerListener} method
     */
    @Override
    public void progress(@NotNull FileReceiver fileReceiver) {
        int proc = (fileReceiver.bytesLoaded() / fileReceiver.bytesTotal()) * 100;
        MODULE_CHAT.addMessage(
                format("%d%% content load %s", proc, fileReceiver.getPath()),
                Color.DARK_GRAY
        );
    }

    /**
     * {@link FileReceiverManager.FileReceiverManagerListener} method
     */
    @Override
    public void complete(@NotNull FileReceiver fileReceiver) {
        MODULE_CHAT.addMessage(
                format("content up-to-date %s", fileReceiver.getPath()),
                Color.DARK_GRAY
        );
    }

    private void setTabWindowVisible(boolean value) {
        tabWindow.removeFromParent();

        if (value) {
            tabWindow.setServerName(serverName, 0, 0);
            tabWindow.setPlayers(
                    MODULE_CLIENT.getLocalPlayerId(),
                    PlayerManager.PLAYER_MANAGER.getPlayerList()
            );

            add(tabWindow);
        }
    }

    private boolean clientCommand(String text) {
        return MODULE_COMMAND_PROCESSOR.process(text);
    }

    private void addToStage(Event event) {
        add(new FpsMeter(), 0, getStage().getStageWidth() - 100);
    }

    public void start(@NotNull String server, String localPlayerName) {
        log.debug("Staring... server: {}, player name: {}", server, localPlayerName);
        if (MODULE_CLIENT.isConnected()) {
            MODULE_CLIENT.close();
        }
        worldScene.init();

        this.server = server;

        String host = server.contains(":") ? server.split(":")[0] : server;
        int port = server.contains(":") ? Integer.parseInt(server.split(":")[1]) : DEFAULT_PORT;

        MODULE_CLIENT.setLocalPlayerName(localPlayerName);

        MODULE_CHAT.addMessage("Connecting to " + server + "...", Color.GRAY);
        MODULE_CLIENT.connect(host, port);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void exit() {
        MODULE_CLIENT.sendExitRequest();
        MODULE_CHAT.dispose();
        new Lock().lock(1, SECONDS);
        DebugPanel.saveAll();
        System.exit(0);
    }

}



























