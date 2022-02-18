/*
 *   D2D2 World Server
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
import ru.ancevt.d2d2world.net.protocol.ExitCause;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImplListener;
import ru.ancevt.d2d2world.server.GlobalTimerListener;
import ru.ancevt.d2d2world.server.ServerInfo;
import ru.ancevt.d2d2world.server.chat.Chat;
import ru.ancevt.d2d2world.server.chat.ChatListener;
import ru.ancevt.d2d2world.server.chat.ChatMessage;
import ru.ancevt.d2d2world.server.player.Player;
import ru.ancevt.d2d2world.server.player.PlayerManager;
import ru.ancevt.net.messaging.connection.IConnection;
import ru.ancevt.net.messaging.server.IServer;

import java.util.List;
import java.util.Optional;

import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessagePlayerEnterResponse;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageRemotePlayerEnter;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageRemotePlayerExit;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageRemotePlayerIntroduce;

@Slf4j
public class GeneralService implements ServerProtocolImplListener, ChatListener, GlobalTimerListener {

    private final PlayerManager playerManager;
    private final IServer serverUnit;
    private final ServerSender sender;
    private final Chat chat;
    private final SyncService syncService;

    public GeneralService(PlayerManager playerManager,
                          ServerProtocolImpl protocolImpl,
                          IServer serverUnit,
                          ServerSender serverSender,
                          Chat chat,
                          SyncService syncService) {

        this.playerManager = playerManager;
        this.serverUnit = serverUnit;
        this.sender = serverSender;
        this.chat = chat;
        this.syncService = syncService;

        protocolImpl.addServerProtocolImplListener(this);
        chat.addChatListener(this);
    }

    @Override
    public void serverInfoRequest(int connectionId) {
        ServerInfo si = ServerInfo.INSTANCE;

        List<Pair<Integer, String>> players =
                playerManager.getPlayerList()
                        .stream()
                        .map(player -> Pair.of(player.getId(), player.getName()))
                        .toList();

        sender.sendToPlayer(connectionId,
                ServerProtocolImpl.createMessageServerInfoResponse(
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
    public void rconLogin(int playerId, String login, String passwordHash) {
        throw new NotImplementedException();
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void rconCommand(int playerId, String commandText, String extraData) {
        throw new NotImplementedException();
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerEnterRequest(int playerId, String playerName, String clientProtocolVersion, String extraData) {
        // save player list before new player actually added to server player list
        List<Player> oldPlayerList = playerManager.getPlayerList();

        // create new player in player manager
        Player newPlayer = playerManager.createPlayer(
                playerId,
                playerName,
                clientProtocolVersion,
                getConnection(playerId).orElseThrow().getRemoteAddress(),
                extraData
        );

        // send to new player info about all others
        oldPlayerList.forEach(p -> sender.sendToPlayer(
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
        sender.sendToAllExcluding(
                createMessageRemotePlayerIntroduce(
                        playerId,
                        playerName,
                        newPlayer.getColor(),
                        newPlayer.getAddress()
                ),
                playerId
        );

        // send to new player info about new player (id, color)
        sender.sendToPlayer(playerId,
                createMessagePlayerEnterResponse(
                        playerId,
                        newPlayer.getColor()
                )
        );

        // send everyone else information about the entrance of a new player
        sender.sendToAllExcluding(
                createMessageRemotePlayerEnter(playerId, playerName, newPlayer.getColor()),
                playerId
        );

        // send chat history to new player
        chat.getMessagesFromIdExcluding(newPlayer.getLastSeenChatMessageId()).forEach(chatMessage -> {
            if (chatMessage.isFromPlayer()) {
                sender.sendToPlayer(
                        playerId,
                        ServerProtocolImpl.createMessageChat(
                                chatMessage.getId(),
                                chatMessage.getText(),
                                chatMessage.getPlayerId(),
                                chatMessage.getPlayerName(),
                                chatMessage.getPlayerColor()
                        )
                );
            } else {
                sender.sendToPlayer(
                        playerId,
                        ServerProtocolImpl.createMessageChat(chatMessage.getId(), chatMessage.getText())
                );
            }

            newPlayer.setLastSeenChatMessageId(chatMessage.getId());
        });

        // send enter message to all players including new player

        chat.text("Player " + playerName + "(" + playerId + ") connected");
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerControllerAndXYReport(int playerId, int controllerState, float x, float y) {
        playerManager.getPlayerById(playerId).ifPresent(player -> {
            player.setControllerState(controllerState);
            player.setXY(x, y);
        });
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerTextToChat(int playerId, String text) {
        playerManager.getPlayerById(playerId).ifPresent(
                player -> chat.playerText(text, player.getId(), player.getName(), player.getColor())
        );
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerExitRequest(int playerId) {
        playerManager.getPlayerById(playerId).ifPresent(p -> {
            playerManager.removePlayer(p);
            chat.text("Player " + p.getName() + "(" + playerId + ") exit");
        });

        getConnection(playerId).orElseThrow().close();
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void errorFromPlayer(int errorCode, String errorMessage, String errorDetails) {
        throw new NotImplementedException();
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerPingRequest(int playerId) {
        sender.sendToPlayer(playerId, ServerProtocolImpl.createMessagePlayerPingResponse());
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerPingReport(int playerId, int ping) {
        playerManager.getPlayerById(playerId).ifPresent(p -> p.setPingValue(ping));
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void extraFromPlayer(int playerId, String extraData) {
        throw new NotImplementedException();
    }

    /**
     * {@link GlobalTimerListener} method
     */
    @Override
    public void globalTimerTick(long count) {
        syncService.syncFirstLevel();
        if (count % 1000 == 0) syncService.syncSecondLevel();
        if (count % 10000 == 0) syncService.syncThirdLevel();
    }

    /**
     * {@link ChatListener} method
     */
    @Override
    public void chatMessage(ChatMessage chatMessage) {
        if (chatMessage.isFromPlayer())
            sender.sendToAll(
                    ServerProtocolImpl.createMessageChat(
                            chatMessage.getId(),
                            chatMessage.getText(),
                            chatMessage.getPlayerId(),
                            chatMessage.getPlayerName(),
                            chatMessage.getPlayerColor()
                    )
            );
        else
            sender.sendToAll(
                    ServerProtocolImpl.createMessageChat(
                            chatMessage.getId(),
                            chatMessage.getText()
                    )
            );

        playerManager.getPlayerList().forEach(p -> p.setLastSeenChatMessageId(chatMessage.getId()));
    }

    public void normalServerExit() {

    }

    public void disconnectPlayer(int playerId) {
        // if the player exists in the player manager and has not been deleted yet, it will be CONNECTION_LOST exit cause
        playerManager.getPlayerById(playerId).ifPresentOrElse(player -> {
                    playerManager.removePlayer(player);
                    sender.sendToAll(createMessageRemotePlayerExit(playerId, ExitCause.LOST_CONNECTION));
                    chat.text("Player " + player.getName() + "(" + playerId + ") lost connection");
                },
                // Or else if player does not exist in player manager, it will be NORMAL_EXIT exit cause
                () -> {
                    sender.sendToAll(createMessageRemotePlayerExit(playerId, ExitCause.NORMAL_EXIT));
                    System.out.println("player normal exited");
                }

        );


    }

    public @NotNull Optional<IConnection> getConnection(int connectionId) {
        return serverUnit.getConnections()
                .stream()
                .filter(c -> c.getId() == connectionId)
                .findAny();
    }
}























