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
import ru.ancevt.d2d2world.net.dto.ServerMapInfoDto;
import ru.ancevt.d2d2world.net.protocol.ExitCause;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImplListener;
import ru.ancevt.d2d2world.net.transfer.FileSender;
import ru.ancevt.d2d2world.net.transfer.Headers;
import ru.ancevt.d2d2world.server.ServerConfig;
import ru.ancevt.d2d2world.server.ServerStateInfo;
import ru.ancevt.d2d2world.server.ServerTimer;
import ru.ancevt.d2d2world.server.ServerTimerListener;
import ru.ancevt.d2d2world.server.chat.ServerChat;
import ru.ancevt.d2d2world.server.chat.ServerChatListener;
import ru.ancevt.d2d2world.server.chat.ServerChatMessage;
import ru.ancevt.d2d2world.server.content.ServerContentManager;
import ru.ancevt.d2d2world.server.player.Player;
import ru.ancevt.d2d2world.server.player.ServerPlayerManager;
import ru.ancevt.d2d2world.server.repl.ServerCommandProcessor;
import ru.ancevt.net.tcpb254.CloseStatus;
import ru.ancevt.net.tcpb254.connection.IConnection;
import ru.ancevt.net.tcpb254.server.IServer;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.*;
import static ru.ancevt.d2d2world.net.transfer.Headers.*;
import static ru.ancevt.d2d2world.server.ServerConfig.CONTENT_COMPRESSION;
import static ru.ancevt.d2d2world.server.ServerStateInfo.MODULE_SERVER_STATE_INFO;
import static ru.ancevt.d2d2world.server.content.ServerContentManager.MODULE_CONTENT_MANAGER;

@Slf4j
public class GeneralService implements ServerProtocolImplListener, ServerChatListener, ServerTimerListener {

    public static final GeneralService MODULE_GENERAL = new GeneralService();

    public static final String NAME_PATTERN = "[\\[\\]()_а-яА-Яa-zA-Z0-9]+";

    private final ServerConfig serverConfig = ServerConfig.MODULE_SERVER_CONFIG;
    private final IServer serverUnit = ServerUnit.MODULE_SERVER_UNIT.server;
    private final ServerTimer serverTimer = ServerTimer.MODULE_TIMER;
    private final SyncService syncService = SyncService.MODULE_SYNC;
    private final ServerChat serverChat = ServerChat.MODULE_CHAT;
    private final ServerSender serverSender = ServerSender.MODULE_SENDER;
    private final ServerPlayerManager serverPlayerManager = ServerPlayerManager.MODULE_PLAYER_MANAGER;
    private final ServerStateInfo serverStateInfo = MODULE_SERVER_STATE_INFO;
    private final ServerCommandProcessor commandProcessor = ServerCommandProcessor.MODULE_COMMAND_PROCESSOR;

    private GeneralService() {
        serverChat.addServerChatListener(this);
        serverTimer.setTimerListener(this);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void serverInfoRequest(int playerId) {
        if (log.isInfoEnabled()) {
            Optional<IConnection> oConnection = getConnection(playerId);
            String address = oConnection.isPresent() ? oConnection.get().getRemoteAddress() : "unknown";
            log.trace("Server info request from connection id {}, address {}", playerId, address);
        }

        List<Pair<Integer, String>> players =
                serverPlayerManager.getPlayerList()
                        .stream()
                        .map(player -> Pair.of(player.getId(), player.getName()))
                        .toList();

        serverSender.sendToPlayer(playerId,
                createMessageServerInfoResponse(
                        serverStateInfo.getName(),
                        serverStateInfo.getVersion(),
                        serverStateInfo.getMap(),
                        serverStateInfo.getMapKit(),
                        serverStateInfo.getMod(),
                        serverStateInfo.getMaxPlayers(),
                        players
                )
        );

        if (!serverPlayerManager.containsPlayer(playerId)) {
            getConnection(playerId).ifPresent(IConnection::closeIfOpen);
        }
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void requestFile(int connectionId, @NotNull String headers) {
        Headers h = Headers.of(headers);
        String path = h.get(PATH);


        if (h.contains(HASH) && h.get(HASH).equals(MD5.hashFile(path))) {
            serverSender.sendToPlayer(connectionId, createMessageFileData(
                    newHeaders()
                            .put(UP_TO_DATE, "true")
                            .put(PATH, path)
                            .toString(), new byte[0]));
        } else {
            FileSender fileSender = new FileSender(path, serverConfig.getBoolean(CONTENT_COMPRESSION), true);
            getConnection(connectionId).ifPresent(fileSender::send);
        }

    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void rconLogin(int playerId, @NotNull String passwordHash) {
        if (MD5.hash(serverConfig.getString(ServerConfig.RCON_PASSWORD)).equals(passwordHash)) {
            serverPlayerManager.getPlayerById(playerId).ifPresent(p -> {
                p.setRconLoggedIn(true);
                if (log.isInfoEnabled()) {
                    log.info("rcon logged in {}({}), address: {}", p.getName(), playerId, p.getAddress());
                }
                serverSender.sendToPlayer(playerId, createMessageTextToPlayer("You are logged in as rcon admin", 0xFFFFFF));
            });
        } else {
            if (log.isInfoEnabled()) {
                serverPlayerManager.getPlayerById(playerId).ifPresent(p -> {
                    log.info("rcon wrong password {}({}), address: {}", p.getName(), playerId, p.getAddress());
                });
            }

            serverSender.sendToPlayer(playerId, createMessageTextToPlayer("Wrong password", 0xFF0000));
        }
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void rconCommand(int playerId, @NotNull String commandText, @NotNull String extraData) {
        serverPlayerManager.getPlayerById(playerId).ifPresent(p -> {
            if (p.isRconLoggedIn()) {
                String rconResponse = commandProcessor.execute(commandText);
                if (log.isInfoEnabled()) {
                    log.info("rcon: {}({}): {}", p.getName(), playerId, commandText);
                    log.info("rcon response: {}({}): {}", p.getName(), playerId, rconResponse);
                }
                serverSender.sendToPlayer(playerId, createMessageRconResponse(rconResponse));
            }
        });
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerEnterRequest(int connectionId,
                                   @NotNull String playerName,
                                   @NotNull String clientProtocolVersion,
                                   @NotNull String extraData) {

        log.trace("Player enter request {}({}), client protocol version: {}, extra data: {}",
                playerName, connectionId, clientProtocolVersion, extraData
        );

        // validate player name
        if (!PatternMatcher.check(playerName, NAME_PATTERN)) {
            // if invalid close connection and return
            log.info("Invalid player name '{}', connection id:{}", playerName, connectionId);
            getConnection(connectionId).ifPresent(IConnection::closeIfOpen);
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
            log.info("Invalid player name '{}' is already taken, connection id:{}", playerName, connectionId);
            getConnection(connectionId).ifPresent(IConnection::closeIfOpen);
            return;
        }

        // create new player in player manager
        Player newPlayer = serverPlayerManager.createPlayer(
                connectionId,
                playerName,
                clientProtocolVersion,
                getConnection(connectionId).orElseThrow().getRemoteAddress(),
                extraData
        );

        log.info("Player enter {}({})", playerName, connectionId);

        // now the connection id is new player id
        // send to new player info about all others
        oldPlayerList.forEach(p -> serverSender.sendToPlayer(
                        connectionId,
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
                        connectionId,
                        playerName,
                        newPlayer.getColor(),
                        newPlayer.getAddress()
                ),
                connectionId
        );

        // send to new player info about new player (id, color)
        serverSender.sendToPlayer(connectionId,
                createMessagePlayerEnterResponse(
                        connectionId,
                        newPlayer.getColor()
                )
        );

        // send everyone else information about the entrance of a new player
        serverSender.sendToAllExcluding(
                createMessageRemotePlayerEnter(connectionId, playerName, newPlayer.getColor()),
                connectionId
        );

        // send to new player current map and mapkit info
        sendCurrentMapInfoToPlayer(connectionId);

        // send chat history to new player
        serverChat.getMessages(10).forEach(serverChatMessage -> {
            if (serverChatMessage.isFromPlayer()) {
                serverSender.sendToPlayer(
                        connectionId,
                        createMessageChat(
                                serverChatMessage.getId(),
                                serverChatMessage.getText(),
                                serverChatMessage.getTextColor(),
                                serverChatMessage.getPlayerId(),
                                serverChatMessage.getPlayerName(),
                                serverChatMessage.getPlayerColor())
                );
            } else {
                serverSender.sendToPlayer(
                        connectionId,
                        createMessageChat(
                                serverChatMessage.getId(),
                                serverChatMessage.getText(),
                                serverChatMessage.getTextColor())
                );
            }

            newPlayer.setLastSeenChatMessageId(serverChatMessage.getId());
        });

        // send enter message to all players including new player
        serverChat.text("Player " + playerName + "(" + connectionId + ") connected", 0xFFFF00);
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
            serverChat.text("Player " + p.getName() + "(" + playerId + ") exit", 0x999999);
            serverPlayerManager.removePlayer(p);
            serverSender.sendToAll(createMessageRemotePlayerExit(playerId, ExitCause.NORMAL_EXIT));

            if (log.isInfoEnabled()) {
                log.info("Exit: {}({}), address: {}", p.getName(), playerId, p.getAddress());
            }
        });

        getConnection(playerId).ifPresent(IConnection::close);
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
    public void playerPingReport(int playerId, int ping) {
        serverPlayerManager.getPlayerById(playerId).ifPresent(p -> p.setPingValue(ping));
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void extraFromPlayer(int playerId, @NotNull String className, String extraDataFromPlayer) {
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
    public void chatMessage(@NotNull ServerChatMessage serverChatMessage) {
        if (serverChatMessage.isFromPlayer())
            serverSender.sendToAll(
                    createMessageChat(
                            serverChatMessage.getId(),
                            serverChatMessage.getText(),
                            serverChatMessage.getTextColor(),
                            serverChatMessage.getPlayerId(),
                            serverChatMessage.getPlayerName(),
                            serverChatMessage.getPlayerColor())
            );
        else
            serverSender.sendToAll(
                    createMessageChat(
                            serverChatMessage.getId(),
                            serverChatMessage.getText(),
                            serverChatMessage.getTextColor())
            );

        serverPlayerManager.getPlayerList().forEach(p -> p.setLastSeenChatMessageId(serverChatMessage.getId()));

        if (log.isInfoEnabled()) {
            log.info("chat {}({}): {}",
                    serverChatMessage.getPlayerName(),
                    serverChatMessage.getPlayerId(),
                    serverChatMessage.getText()
            );
        }
    }

    private void sendCurrentMapInfoToPlayer(int playerId) {
        String mapName = MODULE_SERVER_STATE_INFO.getMap();

        ServerContentManager.Map map = MODULE_CONTENT_MANAGER.getMaps()
                .stream()
                .filter(m -> m.name().equals(mapName))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("no such map on disk: " + mapName));

        var builder =
                ServerMapInfoDto.builder()
                        .name(map.name())
                        .filename(map.filename());

        Set<ServerMapInfoDto.Mapkit> mapkits = new HashSet<>();

        map.mapkits().forEach(
                mk -> {
                    mapkits.add(ServerMapInfoDto.Mapkit.builder()
                                    .uid(mk.uid())
                                    .name(mk.name())
                                    .files(Set.copyOf(mk.files()))
                                    .build());
                }

        );

        builder.mapkits(mapkits);

        serverSender.sendToPlayer(playerId, createMessageExtra(builder.build()));
    }

    private void playerTextCommand(int playerId, @NotNull String commandText) {
        if (log.isInfoEnabled()) {
            serverPlayerManager.getPlayerById(playerId).ifPresent(player -> {
                log.info("cmd {}({}): {}", player.getName(), playerId, commandText);
            });
        }

        serverSender.sendToPlayer(playerId, createMessageTextToPlayer("unknown command: " + commandText, 0xFFFFFF));
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























