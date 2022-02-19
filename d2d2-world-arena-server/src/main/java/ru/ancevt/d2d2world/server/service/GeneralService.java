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
import ru.ancevt.d2d2world.net.protocol.ExitCause;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImplListener;
import ru.ancevt.d2d2world.server.ServerTimerListener;
import ru.ancevt.d2d2world.server.Modules;
import ru.ancevt.d2d2world.server.ServerInfo;
import ru.ancevt.d2d2world.server.chat.ChatListener;
import ru.ancevt.d2d2world.server.chat.ChatMessage;
import ru.ancevt.d2d2world.server.player.Player;
import ru.ancevt.net.messaging.connection.IConnection;

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

@Slf4j
public class GeneralService implements ServerProtocolImplListener, ChatListener, ServerTimerListener {

    public static final GeneralService INSTANCE = new GeneralService();

    public GeneralService() {
        Modules.SERVER_PROTOCOL_IMPL.addServerProtocolImplListener(this);
        Modules.SERVER_CHAT.addChatListener(this);
    }

    @Override
    public void serverInfoRequest(int connectionId) {
        ServerInfo si = ServerInfo.INSTANCE;

        List<Pair<Integer, String>> players =
                Modules.PLAYER_MANAGER.getPlayerList()
                        .stream()
                        .map(player -> Pair.of(player.getId(), player.getName()))
                        .toList();

        Modules.SENDER.sendToPlayer(connectionId,
                createMessageServerInfoResponse(
                        si.getName(),
                        si.getVersion(),
                        si.getMap(),
                        si.getMapKit(),
                        si.getMod(),
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
        if (MD5.hash(Modules.CONFIG.rconPassword()).equals(passwordHash)) {
            Modules.PLAYER_MANAGER.getPlayerById(playerId).ifPresent(p -> p.setRconLoggedIn(true));
            Modules.SENDER.sendToPlayer(playerId, createMessageTextToPlayer("You are logged in as rcon admin"));
        } else {
            Modules.SENDER.sendToPlayer(playerId, createMessageTextToPlayer("Wrong password"));
        }
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void rconCommand(int playerId, @NotNull String commandText, @NotNull String extraData) {
        Modules.COMMAND_PROCESSOR.execute(commandText);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerEnterRequest(int playerId,
                                   @NotNull String playerName,
                                   @NotNull String clientProtocolVersion,
                                   @NotNull String extraData) {
        // save player list before new player actually added to server player list
        List<Player> oldPlayerList = Modules.PLAYER_MANAGER.getPlayerList();

        // create new player in player manager
        Player newPlayer = Modules.PLAYER_MANAGER.createPlayer(
                playerId,
                playerName,
                clientProtocolVersion,
                getConnection(playerId).orElseThrow().getRemoteAddress(),
                extraData
        );

        // send to new player info about all others
        oldPlayerList.forEach(p -> Modules.SENDER.sendToPlayer(
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
        Modules.SENDER.sendToAllExcluding(
                createMessageRemotePlayerIntroduce(
                        playerId,
                        playerName,
                        newPlayer.getColor(),
                        newPlayer.getAddress()
                ),
                playerId
        );

        // send to new player info about new player (id, color)
        Modules.SENDER.sendToPlayer(playerId,
                createMessagePlayerEnterResponse(
                        playerId,
                        newPlayer.getColor()
                )
        );

        // send everyone else information about the entrance of a new player
        Modules.SENDER.sendToAllExcluding(
                createMessageRemotePlayerEnter(playerId, playerName, newPlayer.getColor()),
                playerId
        );

        // send chat history to new player
        Modules.SERVER_CHAT.getMessagesFromIdExcluding(newPlayer.getLastSeenChatMessageId()).forEach(chatMessage -> {
            if (chatMessage.isFromPlayer()) {
                Modules.SENDER.sendToPlayer(
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
                Modules.SENDER.sendToPlayer(
                        playerId,
                        createMessageChat(chatMessage.getId(), chatMessage.getText())
                );
            }

            newPlayer.setLastSeenChatMessageId(chatMessage.getId());
        });

        // send enter message to all players including new player

        Modules.SERVER_CHAT.text("Player " + playerName + "(" + playerId + ") connected");
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerControllerAndXYReport(int playerId, int controllerState, float x, float y) {
        Modules.PLAYER_MANAGER.getPlayerById(playerId).ifPresent(player -> {
            player.setControllerState(controllerState);
            player.setXY(x, y);
        });
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerTextToChat(int playerId, @NotNull String text) {
        Modules.PLAYER_MANAGER.getPlayerById(playerId).ifPresent(
                player -> {
                    if (text.startsWith("/")) {
                        playerTextCommand(playerId, text);
                    } else {
                        Modules.SERVER_CHAT.playerText(text, playerId, player.getName(), player.getColor());
                    }
                }
        );
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerExitRequest(int playerId) {
        Modules.PLAYER_MANAGER.getPlayerById(playerId).ifPresent(p -> {
            Modules.PLAYER_MANAGER.removePlayer(p);
            Modules.SERVER_CHAT.text("Player " + p.getName() + "(" + playerId + ") exit");
        });

        getConnection(playerId).orElseThrow().close();
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
        Modules.SENDER.sendToPlayer(playerId, createMessagePlayerPingResponse());
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerPingReport(int playerId, int ping) {
        Modules.PLAYER_MANAGER.getPlayerById(playerId).ifPresent(p -> p.setPingValue(ping));
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
        Modules.SYNC_SERVICE.syncFirstLevel();
        if (count % 1000 == 0) Modules.SYNC_SERVICE.syncSecondLevel();
        if (count % 10000 == 0) Modules.SYNC_SERVICE.syncThirdLevel();
    }

    /**
     * {@link ChatListener} method
     */
    @Override
    public void chatMessage(ChatMessage chatMessage) {
        if (chatMessage.isFromPlayer())
            Modules.SENDER.sendToAll(
                    createMessageChat(
                            chatMessage.getId(),
                            chatMessage.getText(),
                            chatMessage.getPlayerId(),
                            chatMessage.getPlayerName(),
                            chatMessage.getPlayerColor()
                    )
            );
        else
            Modules.SENDER.sendToAll(
                    createMessageChat(
                            chatMessage.getId(),
                            chatMessage.getText()
                    )
            );

        Modules.PLAYER_MANAGER.getPlayerList().forEach(p -> p.setLastSeenChatMessageId(chatMessage.getId()));
    }

    private void playerTextCommand(int playerId, @NotNull String commandText) {

    }

    public void disconnectPlayer(int playerId) {
        // if the player exists in the player manager and has not been deleted yet, it will be CONNECTION_LOST exit cause
        Modules.PLAYER_MANAGER.getPlayerById(playerId).ifPresentOrElse(player -> {
                    Modules.PLAYER_MANAGER.removePlayer(player);
                    Modules.SENDER.sendToAll(createMessageRemotePlayerExit(playerId, ExitCause.LOST_CONNECTION));
                    Modules.SERVER_CHAT.text("Player " + player.getName() + "(" + playerId + ") lost connection");
                },
                // Or else if player does not exist in player manager, it will be NORMAL_EXIT exit cause
                () -> {
                    Modules.SENDER.sendToAll(createMessageRemotePlayerExit(playerId, ExitCause.NORMAL_EXIT));
                    System.out.println("player normal exited");
                }

        );


    }

    public @NotNull Optional<IConnection> getConnection(int connectionId) {
        return Modules.SERVER_UNIT.getConnections()
                .stream()
                .filter(c -> c.getId() == connectionId)
                .findAny();
    }

    public void exit() {
        Modules.TIMER.stop();
        Modules.SERVER_UNIT.close();
        log.info("Server exit");
        System.exit(0);
    }
}























