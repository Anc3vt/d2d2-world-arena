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
import com.ancevt.d2d2world.desktop.sound.D2D2WorldSound;
import com.ancevt.d2d2world.desktop.ui.TabWindow;
import com.ancevt.d2d2world.desktop.ui.UiTextInputProcessor;
import com.ancevt.d2d2world.desktop.ui.chat.Chat;
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

import static com.ancevt.d2d2world.desktop.ClientCommandProcessor.COMMAND_PROCESSOR;
import static com.ancevt.d2d2world.desktop.DesktopConfig.MODULE_CONFIG;
import static com.ancevt.d2d2world.net.client.Client.CLIENT;
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
    private boolean connected;

    public GameRoot() {
        UiTextInputProcessor.enableRoot(this);

        setBackgroundColor(Color.BLACK);

        CLIENT.addClientListener(this);

        Chat.getInstance().addEventListener(ChatEvent.CHAT_TEXT_ENTER, event -> {
            var e = (ChatEvent) event;
            String text = e.getText();
            if (text.startsWith("/") || text.startsWith("\\")) {
                // if typed command is one of registered client command then don't send that command text to the server
                text = text.replace('\\', '/');
                if (clientCommand(text)) return;
            }
            if (CLIENT.isConnected()) {
                CLIENT.sendChatMessage(text);
            }
        });


        addEventListener(this, InputEvent.KEY_DOWN, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.PAGE_UP -> Chat.getInstance().setScroll(Chat.getInstance().getScroll() - 10);
                case KeyCode.PAGE_DOWN -> Chat.getInstance().setScroll(Chat.getInstance().getScroll() + 10);
                case KeyCode.F8 -> Chat.getInstance().setShadowEnabled(!Chat.getInstance().isShadowEnabled());
                case KeyCode.F6 -> {
                    if (!Chat.getInstance().isInputOpened()) {
                        Chat.getInstance().openInput();
                    } else {
                        Chat.getInstance().closeInput();
                    }
                }
                case KeyCode.TAB -> {
                    Chat.getInstance().setVisible(false);
                    setTabWindowVisible(true);
                }
                case KeyCode.F -> {
                    if (e.isAlt()) D2D2.setFullscreen(!D2D2.isFullscreen());
                }
                case KeyCode.S -> {
                    if (e.isAlt()) D2D2.setSmoothMode(!D2D2.isSmoothMode());
                }
                case KeyCode.T -> {
                    if (!Chat.getInstance().isInputOpened()) {
                        Chat.getInstance().openInput();
                    }
                }
            }
        });

        addEventListener(this, InputEvent.KEY_UP, event -> {
            var e = (InputEvent) event;
            switch (e.getKeyCode()) {
                case KeyCode.TAB -> {
                    Chat.getInstance().setVisible(true);
                    setTabWindowVisible(false);
                }

            }
        });




        worldScene = new WorldScene();
        add(worldScene);

        add(Chat.getInstance(), 10, 10);

        ControlsHelp controlsHelp = new ControlsHelp();
        add(controlsHelp,
                (D2D2.getStage().getStageWidth() - controlsHelp.getWidth()) / 2,
                (D2D2.getStage().getStageHeight() - controlsHelp.getHeight()) / 5
        );

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
        Chat.getInstance().addMessage(text, Color.of(textColor));
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
        rconResponseData.lines().forEach(Chat.getInstance()::addMessage);
    }

    @Override
    public void playerEnterServer(int id, @NotNull String name, int color) {
        D2D2WorldSound.playSound(D2D2WorldSound.PLAYER_ENTER);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void playerExit(@NotNull Player remotePlayer) {
        D2D2WorldSound.playSound(D2D2WorldSound.PLAYER_EXIT);
        worldScene.remotePlayerExit(remotePlayer.getId());
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void localPlayerEnterServer(int localPlayerId,
                                       int localPlayerColor,
                                       @NotNull String serverProtocolVersion,
                                       @NotNull LocalDateTime serverStartTime) {

        Chat.getInstance().addMessage("Your id is " +
                        localPlayerId +
                        ", server protocol version is "
                        + serverProtocolVersion +
                        ". Server started at " + serverStartTime
                , Color.WHITE);
        worldScene.start();

        D2D2World.getAim().setColor(Color.WHITE);

        String rconPassword = MODULE_CONFIG.getString(DesktopConfig.RCON_PASSWORD);
        if (!rconPassword.isEmpty()) {
            CLIENT.sendRconLoginRequest(MD5.hash(rconPassword));
        }

        D2D2WorldSound.playSound(D2D2WorldSound.PLAYER_ENTER);
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void serverChat(int chatMessageId, @NotNull String chatMessageText, int chatMessageTextColor) {
        Chat.getInstance().addMessage("Server: " + chatMessageText, Color.of(chatMessageTextColor));
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

        Chat.getInstance().addPlayerMessage(chatMessageId, playerId, playerName, playerColor, chatMessageText, Color.of(textColor));
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionClosed(@NotNull CloseStatus status) {
        connected = false;
        worldScene.stop();
        Chat.getInstance().addMessage(status.getErrorMessage(), Color.RED);
        new Lock().lock(5, SECONDS);

        if(connected){
            attempts = 0;
            return;
        }

        if (attempts < 10) {
            start(server, CLIENT.getLocalPlayerName());
        } else {
            Chat.getInstance().addMessage("Can't establish connection. Please try again later");
            setBackgroundColor(Color.BLACK);
        }
        attempts++;
    }

    /**
     * {@link ClientListener} method
     */
    @Override
    public void clientConnectionEstablished() {
        Chat.getInstance().addMessage("Connection established", Color.GREEN);
        CLIENT.sendPlayerEnterRequest();
        connected = true;
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
                    Chat.getInstance().addMessage(
                            killerPlayer.getName() + "(" + killerPlayer.getId() + ") killed " +
                                    deadPlayer.getName() + "(" + deadPlayer.getId() + ")");

                    PLAYER_MANAGER.getPlayer(killerPlayerId).orElseThrow().incrementFrags();
                },

                () -> {
                    Chat.getInstance().addMessage(
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
     * {@link ClientListener} method
     */
    @Override
    public void setRoom(String roomId, float cameraX, float cameraY) {
        worldScene.setRoom(roomId, cameraX, cameraY);
    }

    /**
     * {@link FileReceiverManager.FileReceiverManagerListener} method
     */
    @Override
    public void fileReceiverProgress(@NotNull FileReceiver fileReceiver) {
        int proc = (fileReceiver.bytesLoaded() / fileReceiver.bytesTotal()) * 100;
        Chat.getInstance().addMessage(format("%d%% content load %s", proc, fileReceiver.getPath()), Color.DARK_GRAY);
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
        return COMMAND_PROCESSOR.process(text);
    }

    public void start(@NotNull String server, String localPlayerName) {
        log.debug("Staring... server: {}, player name: {}", server, localPlayerName);
        if (CLIENT.isConnected()) {
            CLIENT.close();
        }
        worldScene.init();

        this.server = server;

        String host = server.contains(":") ? server.split(":")[0] : server;
        int port = server.contains(":") ? Integer.parseInt(server.split(":")[1]) : DEFAULT_PORT;

        CLIENT.setLocalPlayerName(localPlayerName);

        Chat.getInstance().addMessage("Connecting to " + server + "...", Color.GRAY);
        CLIENT.connect(host, port);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void exit() {
        CLIENT.sendExitRequest();
        Chat.getInstance().dispose();
        new Lock().lock(1, SECONDS);
        DebugPanel.saveAll();
        System.exit(0);
    }
}

