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
package ru.ancevt.d2d2world.server.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.Pair;
import ru.ancevt.commons.exception.NotImplementedException;
import ru.ancevt.commons.hash.MD5;
import ru.ancevt.commons.regex.PatternMatcher;
import ru.ancevt.d2d2world.net.protocol.ExitCause;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImplListener;
import ru.ancevt.d2d2world.server.Config;
import ru.ancevt.d2d2world.server.ServerStateInfo;
import ru.ancevt.d2d2world.server.ServerTimer;
import ru.ancevt.d2d2world.server.ServerTimerListener;
import ru.ancevt.d2d2world.server.chat.ChatMessage;
import ru.ancevt.d2d2world.server.chat.ServerChat;
import ru.ancevt.d2d2world.server.chat.ServerChatListener;
import ru.ancevt.d2d2world.server.player.Player;
import ru.ancevt.d2d2world.server.player.ServerPlayerManager;
import ru.ancevt.d2d2world.server.repl.ServerCommandProcessor;
import ru.ancevt.net.messaging.CloseStatus;
import ru.ancevt.net.messaging.connection.IConnection;
import ru.ancevt.net.messaging.server.IServer;

import java.util.List;
import java.util.Optional;

import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageChat;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessagePlayerEnterResponse;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessagePlayerPingResponse;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageRemotePlayerEnter;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageRemotePlayerExit;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageRemotePlayerIntroduce;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageServerInfoResponse;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageTextToPlayer;
import static ru.ancevt.d2d2world.server.ModuleContainer.modules;

@Slf4j
public class GeneralService implements ServerProtocolImplListener, ServerChatListener, ServerTimerListener {

    public static final String NAME_PATTERN = "[\\[\\]()_а-яА-Яa-zA-Z0-9]+";

    private final Config config = modules.get(Config.class);
    private final IServer serverUnit = modules.get(ServerUnit.class).server;
    private final ServerTimer serverTimer = modules.get(ServerTimer.class);
    private final SyncService syncService = modules.get(SyncService.class);
    private final ServerChat serverChat = modules.get(ServerChat.class);
    private final ServerSender serverSender = modules.get(ServerSender.class);
    private final ServerPlayerManager serverPlayerManager = modules.get(ServerPlayerManager.class);
    private final ServerStateInfo serverStateInfo = modules.get(ServerStateInfo.class);
    private final ServerCommandProcessor commandProcessor = modules.get(ServerCommandProcessor.class);

    public GeneralService() {
        modules.get(ServerProtocolImpl.class).addServerProtocolImplListener(this);
        modules.get(ServerChat.class).addChatListener(this);
        modules.get(ServerTimer.class).setTimerListener(this);
    }

    @Override
    public void serverInfoRequest(int connectionId) {
        List<Pair<Integer, String>> players =
                serverPlayerManager.getPlayerList()
                        .stream()
                        .map(player -> Pair.of(player.getId(), player.getName()))
                        .toList();

        serverSender.sendToPlayer(connectionId,
                createMessageServerInfoResponse(
                        serverStateInfo.getName(),
                        serverStateInfo.getVersion(),
                        serverStateInfo.getMap(),
                        serverStateInfo.getMapKit(),
                        serverStateInfo.getMod(),
                        players
                )
        );

        getConnection(connectionId).ifPresent(IConnection::closeIfOpen);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void rconLogin(int playerId, @NotNull String passwordHash) {
        if (MD5.hash(config.getString(Config.RCON_PASSWORD)).equals(passwordHash)) {
            serverPlayerManager.getPlayerById(playerId).ifPresent(p -> p.setRconLoggedIn(true));
            serverSender.sendToPlayer(playerId, createMessageTextToPlayer("You are logged in as rcon admin"));
        } else {
            serverSender.sendToPlayer(playerId, createMessageTextToPlayer("Wrong password"));
        }
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void rconCommand(int playerId, @NotNull String commandText, @NotNull String extraData) {
        commandProcessor.execute(commandText);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerEnterRequest(int playerId,
                                   @NotNull String playerName,
                                   @NotNull String clientProtocolVersion,
                                   @NotNull String extraData) {
        // validate player name
        if (!PatternMatcher.check(playerName, NAME_PATTERN)) {
            // if invalid close connection and return
            getConnection(playerId).ifPresent(IConnection::close);
            return;
        }

        // save player list before new player actually added to server player list
        List<Player> oldPlayerList = serverPlayerManager.getPlayerList();


        // check if the same name is already exists
        Optional<Player> player = oldPlayerList.stream()
                .filter(p -> p.getName().equals(playerName))
                .findAny();
        if (player.isPresent()) {
            // if the same name is present close the connection and return
            getConnection(playerId).ifPresent(IConnection::close);
            return;
        }

        // create new player in player manager
        Player newPlayer = serverPlayerManager.createPlayer(
                playerId,
                playerName,
                clientProtocolVersion,
                getConnection(playerId).orElseThrow().getRemoteAddress(),
                extraData
        );

        // send to new player info about all others
        oldPlayerList.forEach(p -> serverSender.sendToPlayer(
                        playerId,
                        createMessageRemotePlayerIntroduce(
                                p.getId(),
                                p.getName(),
                                p.getColor(),
                                p.getAddress()
                        )
                )
        );

        // send to all other player info of new player
        serverSender.sendToAllExcluding(
                createMessageRemotePlayerIntroduce(
                        playerId,
                        playerName,
                        newPlayer.getColor(),
                        newPlayer.getAddress()
                ),
                playerId
        );

        // send to new player info about new player (id, color)
        serverSender.sendToPlayer(playerId,
                createMessagePlayerEnterResponse(
                        playerId,
                        newPlayer.getColor()
                )
        );

        // send everyone else information about the entrance of a new player
        serverSender.sendToAllExcluding(
                createMessageRemotePlayerEnter(playerId, playerName, newPlayer.getColor()),
                playerId
        );

        // send chat history to new player
        serverChat.getMessagesFromIdExcluding(newPlayer.getLastSeenChatMessageId()).forEach(chatMessage -> {
            if (chatMessage.isFromPlayer()) {
                serverSender.sendToPlayer(
                        playerId,
                        createMessageChat(
                                chatMessage.getId(),
                                chatMessage.getText(),
                                chatMessage.getPlayerId(),
                                chatMessage.getPlayerName(),
                                chatMessage.getPlayerColor()
                        )
                );
            } else {
                serverSender.sendToPlayer(
                        playerId,
                        createMessageChat(chatMessage.getId(), chatMessage.getText())
                );
            }

            newPlayer.setLastSeenChatMessageId(chatMessage.getId());
        });

        // send enter message to all players including new player

        serverChat.text("Player " + playerName + "(" + playerId + ") connected");
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerControllerAndXYReport(int playerId, int controllerState, float x, float y) {
        serverPlayerManager.getPlayerById(playerId).ifPresent(player -> {
            player.setControllerState(controllerState);
            player.setXY(x, y);
        });
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerTextToChat(int playerId, @NotNull String text) {
        serverPlayerManager.getPlayerById(playerId).ifPresent(
                player -> {
                    if (text.startsWith("/")) {
                        playerTextCommand(playerId, text);
                    } else {
                        serverChat.playerText(text, playerId, player.getName(), player.getColor());
                    }
                }
        );
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerExitRequest(int playerId) {
        serverPlayerManager.getPlayerById(playerId).ifPresent(p -> {
            serverPlayerManager.removePlayer(p);
            serverChat.text("Player " + p.getName() + "(" + playerId + ") exit");
            serverSender.sendToAll(createMessageRemotePlayerExit(playerId, ExitCause.NORMAL_EXIT));
        });

        getConnection(playerId).ifPresent(IConnection::closeIfOpen);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void errorFromPlayer(int errorCode, @NotNull String errorMessage, @NotNull String errorDetails) {
        throw new NotImplementedException();
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerPingRequest(int playerId) {
        serverSender.sendToPlayer(playerId, createMessagePlayerPingResponse());
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerPingReport(int playerId, int ping) {
        serverPlayerManager.getPlayerById(playerId).ifPresent(p -> p.setPingValue(ping));
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void extraFromPlayer(int playerId, @NotNull String extraData) {
        throw new NotImplementedException();
    }

    /**
     * {@link ServerTimerListener} method
     */
    @Override
    public void globalTimerTick(long count) {
        syncService.syncFirstLevel();
        if (count % 1000 == 0) syncService.syncSecondLevel();
        if (count % 10000 == 0) syncService.syncThirdLevel();
    }

    /**
     * {@link ServerChatListener} method
     */
    @Override
    public void chatMessage(ChatMessage chatMessage) {
        if (chatMessage.isFromPlayer())
            serverSender.sendToAll(
                    createMessageChat(
                            chatMessage.getId(),
                            chatMessage.getText(),
                            chatMessage.getPlayerId(),
                            chatMessage.getPlayerName(),
                            chatMessage.getPlayerColor()
                    )
            );
        else
            serverSender.sendToAll(
                    createMessageChat(
                            chatMessage.getId(),
                            chatMessage.getText()
                    )
            );

        serverPlayerManager.getPlayerList().forEach(p -> p.setLastSeenChatMessageId(chatMessage.getId()));
    }

    private void playerTextCommand(int playerId, @NotNull String commandText) {

    }

    public void connectionClosed(int playerId, CloseStatus status) {
        // if the player exists in the player manager and has not been deleted yet, it will be CONNECTION_LOST exit cause
        serverPlayerManager.getPlayerById(playerId).ifPresent(player -> {
                    serverPlayerManager.removePlayer(player);
                    serverSender.sendToAll(createMessageRemotePlayerExit(playerId, ExitCause.LOST_CONNECTION));
                    serverChat.text("Player " + player.getName() + "(" + playerId + ") lost connection");
                }
        );
    }

    public @NotNull Optional<IConnection> getConnection(int connectionId) {
        return serverUnit.getConnections()
                .stream()
                .filter(c -> c.getId() == connectionId)
                .findAny();
    }

    public void exit() {
        serverTimer.stop();
        serverUnit.close();
        log.info("Server exit");
        System.exit(0);
    }
}























