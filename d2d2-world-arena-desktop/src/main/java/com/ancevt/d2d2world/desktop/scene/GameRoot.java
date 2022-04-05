/*
 *   D2D2 World Arena Desktop
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
package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.commons.concurrent.Lock;
import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.debug.DebugPanel;
import com.ancevt.d2d2world.desktop.DesktopConfig;
import com.ancevt.d2d2world.desktop.ui.TabWindow;
import com.ancevt.d2d2world.desktop.ui.UiTextInputProcessor;
import com.ancevt.d2d2world.desktop.ui.chat.ChatEvent;
import com.ancevt.d2d2world.net.client.ClientListener;
import com.ancevt.d2d2world.net.client.Player;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.d2d2world.net.transfer.FileReceiver;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.net.CloseStatus;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import static com.ancevt.d2d2world.desktop.ClientCommandProcessor.MODULE_COMMAND_PROCESSOR;
import static com.ancevt.d2d2world.desktop.DesktopConfig.MODULE_CONFIG;
import static com.ancevt.d2d2world.desktop.ui.chat.Chat.MODULE_CHAT;
import static com.ancevt.d2d2world.net.client.Client.MODULE_CLIENT;
import static com.ancevt.d2d2world.net.client.PlayerManager.PLAYER_MANAGER;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

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


        addEventListener(this, InputEvent.KEY_DOWN, event -> {
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
                case KeyCode.F -> {
                    if (e.isAlt()) D2D2.setFullscreen(!D2D2.isFullscreen());
                }
                case KeyCode.S -> {
                    if (e.isAlt()) D2D2.setSmoothMode(!D2D2.isSmoothMode());
                }
                case KeyCode.T -> {
                    if (!MODULE_CHAT.isInputOpened()) {
                        MODULE_CHAT.openInput();
                    }
                }
            }
        });

        addEventListener(this, InputEvent.KEY_UP, event -> {
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

        FpsMeter fpsMeter = new FpsMeter();
        add(fpsMeter, D2D2.getStage().getStageWidth() - 50, 2);

        tabWindow = new TabWindow();

        FileReceiverManager.INSTANCE.addFileReceiverManagerListener(this);

        INSTANCE = this;
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverInfo(@NotNull ServerInfoDto dto) {
        tabWindow.setServerInfo(dto.getName(), dto.getPlayers().size(), dto.getMaxPlayers());
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
    public void playerExit(@NotNull Player remotePlayer) {

    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerEnterServer(int localPlayerId,
                                  int localPlayerColor,
                                  @NotNull String serverProtocolVersion,
                                  @NotNull LocalDateTime serverStartTime) {

        MODULE_CHAT.addMessage("Your id is " +
                        localPlayerId +
                        ", server protocol version is "
                        + serverProtocolVersion +
                        ". Server started at " + serverStartTime
                , Color.WHITE);
        worldScene.start();

        D2D2World.getAim().setColor(Color.WHITE);

        String rconPassword = MODULE_CONFIG.getString(DesktopConfig.RCON_PASSWORD);
        if (!rconPassword.isEmpty()) {
            MODULE_CLIENT.sendRconLoginRequest(MD5.hash(rconPassword));
        }
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

        MODULE_CHAT.addPlayerMessage(chatMessageId, playerId, playerName, playerColor, chatMessageText, Color.of(textColor));
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
    public void playerEnterServer(int remotePlayerId, @NotNull String remotePlayerName, int remotePlayerColor) {

    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void mapContentLoaded(String mapFilename) {
        worldScene.loadMap(mapFilename);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void localPlayerActorGameObjectId(int playerActorGameObjectId) {
        worldScene.setLocalPlayerActorGameObjectId(playerActorGameObjectId);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerDeath(int deadPlayerId, int killerPlayerId) {
        Player deadPlayer = PLAYER_MANAGER.getPlayer(deadPlayerId).orElseThrow();

        PLAYER_MANAGER.getPlayer(killerPlayerId).ifPresentOrElse(killerPlayer -> {
                    MODULE_CHAT.addMessage(
                            killerPlayer.getName() + "(" + killerPlayer.getId() + ") killed " +
                                    deadPlayer.getName() + "(" + deadPlayer.getId() + ")");

                    PLAYER_MANAGER.getPlayer(killerPlayerId).orElseThrow().incrementFrags();
                },

                () -> {
                    MODULE_CHAT.addMessage(
                            deadPlayer.getName() + "(" + deadPlayer.getId() + ") knocked out");

                    PLAYER_MANAGER.getPlayer(deadPlayerId).orElseThrow().decrementFrags();
                });
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerChatEvent(int playerId, String action) {
        worldScene.playerChatEvent(playerId, action);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerEnterRoomStartResponseReceived() {
        worldScene.playerEnterRoomStartResponseReceived();
    }

    /**
     * {@link FileReceiverManager.FileReceiverManagerListener} method
     */
    @Override
    public void fileReceiverProgress(@NotNull FileReceiver fileReceiver) {
        int proc = (fileReceiver.bytesLoaded() / fileReceiver.bytesTotal()) * 100;
        MODULE_CHAT.addMessage(format("%d%% content load %s", proc, fileReceiver.getPath()), Color.DARK_GRAY);
    }

    /**
     * {@link FileReceiverManager.FileReceiverManagerListener} method
     */
    @Override
    public void fileReceiverComplete(@NotNull FileReceiver fileReceiver) {

    }

    private void setTabWindowVisible(boolean value) {
        tabWindow.removeFromParent();

        if (value) {
            tabWindow.setPlayers(PLAYER_MANAGER.getPlayerList());
            add(tabWindow);
        }
    }

    private boolean clientCommand(String text) {
        return MODULE_COMMAND_PROCESSOR.process(text);
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

