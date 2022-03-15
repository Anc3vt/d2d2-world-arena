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
package com.ancevt.d2d2world.server.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import com.ancevt.commons.hash.MD5;
import com.ancevt.commons.regex.PatternMatcher;
import com.ancevt.d2d2world.mapkit.CharacterMapkit;
import com.ancevt.d2d2world.net.dto.ChatMessageDto;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.dto.PlayerDto;
import com.ancevt.d2d2world.net.dto.client.*;
import com.ancevt.d2d2world.net.dto.server.*;
import com.ancevt.d2d2world.net.protocol.ExitCause;
import com.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import com.ancevt.d2d2world.net.protocol.ServerProtocolImplListener;
import com.ancevt.d2d2world.net.protocol.SyncDataAggregator;
import com.ancevt.d2d2world.net.transfer.FileSender;
import com.ancevt.d2d2world.net.transfer.Headers;
import com.ancevt.d2d2world.server.ServerConfig;
import com.ancevt.d2d2world.server.ServerState;
import com.ancevt.d2d2world.server.chat.ChatMessage;
import com.ancevt.d2d2world.server.chat.ServerChat;
import com.ancevt.d2d2world.server.chat.ServerChatListener;
import com.ancevt.d2d2world.server.content.ServerContentManager;
import com.ancevt.d2d2world.server.player.Player;
import com.ancevt.d2d2world.server.player.ServerPlayerManager;
import com.ancevt.d2d2world.server.repl.ServerCommandProcessor;
import com.ancevt.net.tcpb254.CloseStatus;
import com.ancevt.net.tcpb254.connection.IConnection;
import com.ancevt.net.tcpb254.server.IServer;

import java.util.*;

import static com.ancevt.d2d2world.net.protocol.ServerProtocolImpl.*;
import static com.ancevt.d2d2world.net.transfer.Headers.*;
import static com.ancevt.d2d2world.server.ServerConfig.CONTENT_COMPRESSION;
import static com.ancevt.d2d2world.server.ServerState.MODULE_SERVER_STATE;
import static com.ancevt.d2d2world.server.content.ServerContentManager.MODULE_CONTENT_MANAGER;
import static com.ancevt.d2d2world.server.player.BanList.MODULE_BANLIST;
import static com.ancevt.d2d2world.server.player.ServerPlayerManager.MODULE_PLAYER_MANAGER;
import static com.ancevt.d2d2world.server.service.ServerSender.MODULE_SENDER;
import static com.ancevt.d2d2world.server.simulation.ServerWorldScene.MODULE_WORLD_SCENE;

@Slf4j
public class GeneralService implements ServerProtocolImplListener, ServerChatListener {

    public static final GeneralService MODULE_GENERAL = new GeneralService();

    public static final String NAME_PATTERN = "[\\[\\]()_а-яА-Яa-zA-Z0-9]+";

    private final ServerConfig serverConfig = ServerConfig.MODULE_SERVER_CONFIG;
    private final IServer serverUnit = ServerUnit.MODULE_SERVER_UNIT.server;
    private final ServerChat serverChat = ServerChat.MODULE_CHAT;
    private final ServerSender serverSender = MODULE_SENDER;
    private final ServerPlayerManager serverPlayerManager = MODULE_PLAYER_MANAGER;
    private final ServerState serverStateInfo = MODULE_SERVER_STATE;
    private final ServerCommandProcessor commandProcessor = ServerCommandProcessor.MODULE_COMMAND_PROCESSOR;

    private GeneralService() {
        serverChat.addServerChatListener(this);
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
    public void playerController(int playerId, int controllerState) {
        MODULE_WORLD_SCENE.playerController(playerId, controllerState);
    }

    @Override
    public void ping(int playerId) {
        MODULE_SENDER.sendToPlayer(playerId, createMessagePing());
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void dtoFromPlayer(int playerId, Dto dto) {

        if (dto instanceof PlayerEnterRequestDto d) {
            String playerName = d.getName();
            String clientProtocolVersion = d.getProtocolVersion();

            if (MODULE_BANLIST.ifBannedCloseConnection(getConnection(playerId).orElseThrow())) {
                log.info("Banned ip enter attempt: {}", playerId);
                return;
            }


            log.trace("Player enter request {}({}), client protocol version: {}",
                    playerName, playerId, clientProtocolVersion
            );

            // validate player name
            if (!PatternMatcher.check(playerName, NAME_PATTERN)) {
                // if invalid close connection and return
                log.info("Invalid player name '{}', connection id:{}", playerName, playerId);
                getConnection(playerId).ifPresent(IConnection::closeIfOpen);
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
                log.info("Invalid player name '{}' is already taken, connection id:{}", playerName, playerId);
                getConnection(playerId).ifPresent(IConnection::closeIfOpen);
                return;
            }

            // create new player in player manager
            Player newPlayer = serverPlayerManager.createPlayer(
                    playerId,
                    playerName,
                    clientProtocolVersion,
                    getConnection(playerId).orElseThrow().getRemoteAddress()
            );

            MODULE_WORLD_SCENE.addPlayer(newPlayer);

            log.info("Player enter {}({})", playerName, playerId);

            // now the connection id is new player id
            // send to new player current state info about server
            serverSender.sendToPlayer(playerId, getServerInfoDto());

            // send to new player info about new player (id, color)
            serverSender.sendToPlayer(playerId,
                    PlayerEnterResponseDto.builder()
                            .player(PlayerDto.builder()
                                    .id(playerId)
                                    .color(newPlayer.getColor())
                                    .name(newPlayer.getName())
                                    .build())
                            .color(newPlayer.getColor())
                            .protocolVersion(PROTOCOL_VERSION)
                            .serverStartTime(MODULE_SERVER_STATE.getStartTime())
                            .build()
            );

            // send everyone else information about the entrance of a new player
            serverSender.sendToAllExcluding(
                    PlayerEnterDto.builder()
                            .player(PlayerDto.builder()
                                    .id(playerId)
                                    .name(newPlayer.getName())
                                    .color(newPlayer.getColor())
                                    .playerActorGameObjectId(MODULE_WORLD_SCENE.getPlayerActorGameObjectId(playerId))
                                    .build())
                            .build(),
                    playerId);

            // send to new player current map and mapkit info
            sendCurrentMapContentInfoToPlayer(playerId);

            // send chat history to new player

            List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
            new ArrayList<>(serverChat.getMessages(10)).forEach(
                    c -> chatMessageDtos.add(
                            ChatMessageDto.builder()
                                    .id(c.getId())
                                    .player(PlayerDto.builder()
                                            .name(c.getPlayerName())
                                            .id(c.getPlayerId())
                                            .color(c.getPlayerColor())
                                            .build())
                                    .textColor(c.getTextColor())
                                    .text(c.getText())
                                    .build())
            );
            serverSender.sendToPlayer(playerId,
                    ChatDto.builder()
                            .messages(chatMessageDtos)
                            .build()
            );

            // send enter message to all players including new player
            serverChat.text("Player " + playerName + "(" + playerId + ") connected", 0xFFFF00);
        }


        if (dto instanceof MapLoadedReport) {
            MODULE_WORLD_SCENE.getWorld().getSyncGameObjects().forEach(
                    o -> MODULE_SENDER.sendToPlayer(playerId, new SyncDataAggregator().createSyncMessage(o)));

            MODULE_SENDER.sendToPlayer(playerId,
                    PlayerActorDto.builder()
                            .playerActorGameObjectId(MODULE_WORLD_SCENE.getPlayerActorGameObjectId(playerId))
                            .build());
        }

        if (dto instanceof ServerInfoRequestDto) {
            serverSender.sendToPlayer(playerId, getServerInfoDto());
        }

        if (dto instanceof RconLoginRequestDto d) {
            String serverRconPasswordHash = MD5.hash(serverConfig.getString(ServerConfig.RCON_PASSWORD));
            if (serverRconPasswordHash.equals(d.getPasswordHash())) {
                serverPlayerManager.getPlayerById(playerId).ifPresent(p -> {
                    if (!p.isRconLoggedIn()) {
                        if (log.isInfoEnabled()) {
                            log.info("rcon logged in {}({}), address: {}", p.getName(), playerId, p.getAddress());
                        }
                        serverSender.sendToPlayer(playerId,
                                ServerTextDto.builder()
                                        .text("Rcon: You are logged in as rcon admin")
                                        .color(0xFFFFFF)
                                        .build());

                        p.setRconLoggedIn(true);
                    } else {
                        serverSender.sendToPlayer(playerId,
                                ServerTextDto.builder()
                                        .text("Rcon: You are already logged in as rcon admin")
                                        .color(0xFFFFFF)
                                        .build());
                    }
                });
            } else {
                if (log.isInfoEnabled()) {
                    serverPlayerManager.getPlayerById(playerId).ifPresent(p -> {
                        log.info("rcon wrong password {}({}), address: {}", p.getName(), playerId, p.getAddress());
                    });
                }

                serverSender.sendToPlayer(playerId,
                        ServerTextDto.builder()
                                .text("Rcon: Wrong rcon password")
                                .color(0xFF0000)
                                .build());
            }
        }

        if (dto instanceof RconCommandDto d) {
            serverPlayerManager.getPlayerById(playerId).ifPresent(p -> {
                if (p.isRconLoggedIn()) {
                    String commandText = d.getCommandText();
                    String rconResponse = commandProcessor.execute(commandText);
                    if (log.isInfoEnabled()) {
                        log.info("rcon: {}({}): {}", p.getName(), playerId, commandText);
                        log.info("rcon response: {}({}): {}", p.getName(), playerId, rconResponse);
                    }
                    serverSender.sendToPlayer(playerId,
                            RconResponseDto.builder()
                                    .text(rconResponse)
                                    .build()
                    );
                }
            });
        }

        if (dto instanceof PlayerTextToChatDto d) {
            String text = d.getText();
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

        if (dto instanceof PlayerExitRequestDto) {
            serverPlayerManager.getPlayerById(playerId).ifPresent(player -> {
                MODULE_WORLD_SCENE.removePlayer(player);

                serverChat.text("Player " + player.getName() + "(" + playerId + ") exit", 0x999999);
                serverPlayerManager.removePlayer(player);
                serverSender.sendToAll(PlayerExitDto.builder()
                        .player(PlayerDto.builder()
                                .id(playerId)
                                .name(player.getName())
                                .build()
                        )
                        .exitCause(ExitCause.NORMAL_EXIT)
                        .build()
                );


                if (log.isInfoEnabled()) {
                    log.info("Exit: {}({}), address: {}", player.getName(), playerId, player.getAddress());
                }
            });

            getConnection(playerId).ifPresent(IConnection::close);
        }

        if (dto instanceof PlayerPingReportDto d) {
            serverPlayerManager.getPlayerById(playerId).ifPresent(p -> p.setPingValue(d.getPing()));
        }


    }

    /**
     * {@link ServerChatListener} method
     */
    @Override
    public void chatMessage(@NotNull ChatMessage serverChatMessage) {
        serverSender.sendToAll(ChatDto.builder()
                .messages(List.of(
                        ChatMessageDto.builder()
                                .id(serverChatMessage.getId())
                                .text(serverChatMessage.getText())
                                .textColor(serverChatMessage.getTextColor())
                                .player(PlayerDto.builder()
                                        .id(serverChatMessage.getPlayerId())
                                        .name(serverChatMessage.getPlayerName())
                                        .color(serverChatMessage.getPlayerColor())
                                        .build())
                                .build()))
                .build());

        serverPlayerManager.getPlayerList().forEach(p -> p.setLastSeenChatMessageId(serverChatMessage.getId()));

        if (log.isInfoEnabled()) {
            log.info("chat {}({}): {}",
                    serverChatMessage.getPlayerName(),
                    serverChatMessage.getPlayerId(),
                    serverChatMessage.getText()
            );
        }
    }

    public ServerInfoDto getServerInfoDto() {
        return ServerInfoDto.builder()
                .name(MODULE_SERVER_STATE.getName())
                .serverVersion(MODULE_SERVER_STATE.getVersion())
                .protocolVersion(ServerProtocolImpl.PROTOCOL_VERSION)
                .currentMap(MODULE_SERVER_STATE.getMap())
                .modName(MODULE_SERVER_STATE.getMod())
                .maxPlayers(MODULE_SERVER_STATE.getMaxPlayers())
                .players(getPlayerDtos())
                .build();
    }

    public Set<PlayerDto> getPlayerDtos() {
        Set<PlayerDto> players = new HashSet<>();

        serverPlayerManager.getPlayerList().forEach(
                player -> players.add(PlayerDto.builder()
                        .id(player.getId())
                        .name(player.getName())
                        .color(player.getColor())
                        .ping(player.getPingValue())
                        .frags(player.getFrags())
                        .playerActorGameObjectId(MODULE_WORLD_SCENE.getPlayerActorGameObjectId(player.getId()))
                        .build()
                ));
        return players;
    }

    public void setMap(String mapName) {
        if (MODULE_CONTENT_MANAGER.containsMap(mapName)) {
            MODULE_SERVER_STATE.setMap(mapName);
            MODULE_WORLD_SCENE.loadMap(mapName);
            MODULE_PLAYER_MANAGER.getPlayerList().forEach(MODULE_WORLD_SCENE::addPlayer);
            sendCurrentMapContentInfoToAll();
        } else {
            throw new IllegalStateException("no such map '" + mapName + "'");
        }
    }

    private ServerContentInfoDto createServerContentDto(String mapName) {
        ServerContentManager.Map map = MODULE_CONTENT_MANAGER.getMaps()
                .stream()
                .filter(m -> m.name().equals(mapName))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("no such map on disk: " + mapName));

        var builder =
                ServerContentInfoDto.builder()
                        .name(map.name())
                        .filename(map.filename());

        Set<ServerContentInfoDto.Mapkit> mapkits = new HashSet<>();

        map.mapkits().forEach(
                mk -> mapkits.add(ServerContentInfoDto.Mapkit.builder()
                        .uid(mk.uid())
                        .name(mk.name())
                        .files(Set.copyOf(mk.files()))
                        .build())
        );

        var characterMapkitDto = ServerContentInfoDto.Mapkit.builder()
                .name(CharacterMapkit.NAME)
                .uid(CharacterMapkit.UID)
                .files(MODULE_CONTENT_MANAGER.getMapkits()
                        .stream()
                        .filter(m -> m.uid().equals(CharacterMapkit.UID))
                        .findAny()
                        .orElseThrow()
                        .files()
                )
                .build();

        mapkits.add(characterMapkitDto);

        builder.mapkits(mapkits);

        return builder.build();
    }

    private void sendCurrentMapContentInfoToAll() {
        String mapName = MODULE_SERVER_STATE.getMap();
        serverSender.sendToAll(createServerContentDto(mapName));
    }

    private void sendCurrentMapContentInfoToPlayer(int playerId) {
        String mapName = MODULE_SERVER_STATE.getMap();
        serverSender.sendToPlayer(playerId, createServerContentDto(mapName));
    }

    private void playerTextCommand(int playerId, @NotNull String commandText) {
        if (log.isInfoEnabled()) {
            serverPlayerManager.getPlayerById(playerId).ifPresent(player -> {
                log.info("cmd {}({}): {}", player.getName(), playerId, commandText);
            });
        }

        serverSender.sendToPlayer(playerId, ServerTextDto.builder()
                .text("unknown command: " + commandText)
                .color(0xFFFFFF)
                .build()
        );
    }

    public void connectionClosed(int playerId, CloseStatus status) {
        // if the player exists in the player manager and has not been deleted yet, it will be CONNECTION_LOST exit cause
        serverPlayerManager.getPlayerById(playerId).ifPresent(player -> {
            MODULE_WORLD_SCENE.removePlayer(player);
            serverPlayerManager.removePlayer(player);
            serverSender.sendToAll(PlayerExitDto.builder()
                    .player(PlayerDto.builder()
                            .id(playerId)
                            .name(player.getName())
                            .build()
                    )
                    .exitCause(ExitCause.LOST_CONNECTION)
                    .build()
            );
            serverChat.text("Player " + player.getName() + "(" + playerId + ") lost connection");
        });
    }

    public @NotNull Optional<IConnection> getConnection(int connectionId) {
        return serverUnit.getConnections()
                .stream()
                .filter(c -> c.getId() == connectionId)
                .findAny();
    }

    public void exit() {
        serverUnit.close();
        log.info("Server exit");
        System.exit(0);
    }
}






















