/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2world.server.service;

import com.ancevt.commons.hash.MD5;
import com.ancevt.commons.regex.PatternMatcher;
import com.ancevt.d2d2world.mapkit.BuiltInMapkit;
import com.ancevt.d2d2world.net.dto.ChatMessageDto;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.dto.PlayerDto;
import com.ancevt.d2d2world.net.dto.client.PlayerActorRequestDto;
import com.ancevt.d2d2world.net.dto.client.PlayerChatEventDto;
import com.ancevt.d2d2world.net.dto.client.PlayerEnterRequestDto;
import com.ancevt.d2d2world.net.dto.client.PlayerEnterRoomStartDto;
import com.ancevt.d2d2world.net.dto.client.PlayerExitRequestDto;
import com.ancevt.d2d2world.net.dto.client.PlayerPingReportDto;
import com.ancevt.d2d2world.net.dto.client.PlayerReadyToSpawnDto;
import com.ancevt.d2d2world.net.dto.client.PlayerTextToChatDto;
import com.ancevt.d2d2world.net.dto.client.RconCommandDto;
import com.ancevt.d2d2world.net.dto.client.RconLoginRequestDto;
import com.ancevt.d2d2world.net.dto.client.RoomSwitchCompleteDto;
import com.ancevt.d2d2world.net.dto.client.ServerInfoRequestDto;
import com.ancevt.d2d2world.net.dto.server.ChatDto;
import com.ancevt.d2d2world.net.dto.server.MapContentInfoDto;
import com.ancevt.d2d2world.net.dto.server.PlayerActorDto;
import com.ancevt.d2d2world.net.dto.server.PlayerEnterResponseDto;
import com.ancevt.d2d2world.net.dto.server.PlayerEnterServerDto;
import com.ancevt.d2d2world.net.dto.server.PlayerExitDto;
import com.ancevt.d2d2world.net.dto.server.RconResponseDto;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.d2d2world.net.dto.server.ServerTextDto;
import com.ancevt.d2d2world.net.dto.service.LocalServerKillDto;
import com.ancevt.d2d2world.net.protocol.ExitCause;
import com.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import com.ancevt.d2d2world.net.protocol.ServerProtocolImplListener;
import com.ancevt.d2d2world.net.transfer.FileSender;
import com.ancevt.d2d2world.net.transfer.Headers;
import com.ancevt.d2d2world.server.ServerState;
import com.ancevt.d2d2world.server.chat.ChatMessage;
import com.ancevt.d2d2world.server.chat.ServerChat;
import com.ancevt.d2d2world.server.chat.ServerChatListener;
import com.ancevt.d2d2world.server.content.ServerContentManager;
import com.ancevt.d2d2world.server.player.Player;
import com.ancevt.d2d2world.server.player.ServerPlayerManager;
import com.ancevt.d2d2world.server.repl.ServerCommandProcessor;
import com.ancevt.net.CloseStatus;
import com.ancevt.net.connection.IConnection;
import com.ancevt.net.server.IServer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.ancevt.d2d2world.net.protocol.ServerProtocolImpl.PROTOCOL_VERSION;
import static com.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessageFileData;
import static com.ancevt.d2d2world.net.protocol.ServerProtocolImpl.createMessagePing;
import static com.ancevt.d2d2world.net.transfer.Headers.HASH;
import static com.ancevt.d2d2world.net.transfer.Headers.PATH;
import static com.ancevt.d2d2world.net.transfer.Headers.UP_TO_DATE;
import static com.ancevt.d2d2world.net.transfer.Headers.newHeaders;
import static com.ancevt.d2d2world.server.ServerState.MODULE_SERVER_STATE;
import static com.ancevt.d2d2world.server.config.ServerConfig.CONFIG;
import static com.ancevt.d2d2world.server.config.ServerConfig.CONTENT_COMPRESSION;
import static com.ancevt.d2d2world.server.config.ServerConfig.RCON_PASSWORD;
import static com.ancevt.d2d2world.server.content.ServerContentManager.MODULE_CONTENT_MANAGER;
import static com.ancevt.d2d2world.server.player.BanList.BANLIST;
import static com.ancevt.d2d2world.server.player.ServerPlayerManager.PLAYER_MANAGER;
import static com.ancevt.d2d2world.server.scene.ServerWorldScene.SERVER_WORLD_SCENE;
import static com.ancevt.d2d2world.server.service.ServerSender.SENDER;

@Slf4j
public class GeneralService implements ServerProtocolImplListener, ServerChatListener {

    public static final GeneralService MODULE_GENERAL = new GeneralService();

    public static final String NAME_PATTERN = "[\\[\\]()_а-яА-Яa-zA-Z0-9]+";

    private final IServer serverUnit = ServerUnit.MODULE_SERVER_UNIT.server;
    private final ServerChat serverChat = ServerChat.MODULE_CHAT;
    private final ServerSender serverSender = SENDER;
    private final ServerPlayerManager serverPlayerManager = PLAYER_MANAGER;
    private final ServerState serverStateInfo = MODULE_SERVER_STATE;
    private final ServerCommandProcessor commandProcessor = ServerCommandProcessor.MODULE_COMMAND_PROCESSOR;

    private final Map<Integer, String> playerMapkitItemIdMap;

    private GeneralService() {
        playerMapkitItemIdMap = new HashMap<>();
        serverChat.addServerChatListener(this);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void requestFile(int connectionId, @NotNull String headers) {
        Headers h = Headers.of(headers);
        String path = h.get(PATH);

        log.trace(headers);

        if (h.contains(HASH) && h.get(HASH).equals(MD5.hashFile(path))) {
            serverSender.sendToPlayer(connectionId, createMessageFileData(
                    newHeaders()
                            .put(UP_TO_DATE, "true")
                            .put(PATH, path)
                            .toString(), new byte[0]));
        } else {
            FileSender fileSender = new FileSender(path, CONFIG.getBoolean(CONTENT_COMPRESSION, true), true);
            getConnection(connectionId).ifPresent(fileSender::send);
        }
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerController(int playerId, int controllerState) {
        SERVER_WORLD_SCENE.playerController(playerId, controllerState);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerXY(int connectionId, float x, float y) {
       SERVER_WORLD_SCENE.playerXY(connectionId, x, y);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerHook(int connectionId, int hookGameObjectId) {
        SERVER_WORLD_SCENE.playerHook(connectionId, hookGameObjectId);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerAimXY(int playerId, float x, float y) {
        SERVER_WORLD_SCENE.playerAimXY(playerId, x, y);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerWeaponSwitch(int playerId, int delta) {
        SERVER_WORLD_SCENE.playerWeaponSwitch(playerId, delta);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void playerHealthReport(int connectionId, int healthValue, int damagingGameObjectId) {
        SERVER_WORLD_SCENE.playerHealthReport(connectionId, healthValue, damagingGameObjectId);
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void ping(int playerId) {
        SENDER.sendToPlayer(playerId, createMessagePing());
    }

    /**
     * {@link ServerProtocolImplListener} method
     */
    @Override
    public void dtoFromPlayer(int playerId, Dto dto) {

        if (dto instanceof PlayerEnterRequestDto d) {
            IConnection connection = getConnection(playerId).orElseThrow();

            String playerName = d.getName();
            String clientProtocolVersion = d.getProtocolVersion();

            if (BANLIST.ifBannedCloseConnection(getConnection(playerId).orElseThrow())) {
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
                connection.closeIfOpen();
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
                connection.closeIfOpen();
                return;
            }

            // create new player in player manager
            Player newPlayer = serverPlayerManager.createPlayer(
                    connection,
                    playerId,
                    playerName,
                    clientProtocolVersion
            );

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
                    PlayerEnterServerDto.builder()
                            .player(PlayerDto.builder()
                                    .id(playerId)
                                    .name(newPlayer.getName())
                                    .color(newPlayer.getColor())
                                    .playerActorGameObjectId(SERVER_WORLD_SCENE.getPlayerActorGameObjectId(playerId))
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

        } else if (dto instanceof PlayerReadyToSpawnDto d) {
            playerMapkitItemIdMap.put(playerId, d.getMapkitItemId());

            int playerActorGameObjectId = SERVER_WORLD_SCENE.spawnPlayerFirstTime(
                    PLAYER_MANAGER.getPlayerById(playerId).orElseThrow(),
                    d.getMapkitItemId()
            );

            serverSender.sendToPlayer(playerId, getServerInfoDto());
            serverSender.sendToPlayer(playerId, PlayerActorDto.builder()
                            .playerActorGameObjectId(playerActorGameObjectId)
                            .build());

        } else if (dto instanceof ServerInfoRequestDto) {
            serverSender.sendToPlayer(playerId, getServerInfoDto());

        } else if (dto instanceof RconLoginRequestDto d) {
            String serverRconPasswordHash = MD5.hash(CONFIG.getProperty(RCON_PASSWORD));
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

        } else if (dto instanceof RconCommandDto d) {
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

        } else if (dto instanceof PlayerTextToChatDto d) {
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

        } else if (dto instanceof PlayerExitRequestDto) {
            serverPlayerManager.getPlayerById(playerId).ifPresent(player -> {
                SERVER_WORLD_SCENE.removePlayer(player);

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

        } else if (dto instanceof PlayerPingReportDto d) {
            serverPlayerManager.getPlayerById(playerId).ifPresent(p -> p.setPingValue(d.getPing()));

        } else if (dto instanceof PlayerChatEventDto d) {
            serverSender.sendToAllExcluding(d, playerId);

        } else if (dto instanceof LocalServerKillDto) {
            getConnection(playerId).ifPresent(c -> {
                if (IConnection.getIpFromAddress(c.getRemoteAddress()).equals("127.0.0.1")) {
                    exit();
                }
            });
        } else if (dto instanceof PlayerEnterRoomStartDto d) {
            SERVER_WORLD_SCENE.changePlayerRoom(playerId, d.getRoomId(), d.getX(), d.getY());
        } else if (dto instanceof RoomSwitchCompleteDto) {
            SERVER_WORLD_SCENE.getPlayerActorByPlayerId(playerId).ifPresent(playerActor -> playerActor.setVisible(true));
            SERVER_WORLD_SCENE.sendGameObjectsSyncData(playerId);
        } else if (dto instanceof PlayerActorRequestDto) {
            SERVER_WORLD_SCENE.sendGameObjectsSyncData(playerId);
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
                        .playerActorGameObjectId(SERVER_WORLD_SCENE.getPlayerActorGameObjectId(player.getId()))
                        .build()
                ));
        return players;
    }

    public void setMap(String mapName) {
        if (MODULE_CONTENT_MANAGER.containsMap(mapName)) {
            MODULE_SERVER_STATE.setMap(mapName);
            SERVER_WORLD_SCENE.loadMap(mapName);
            sendCurrentMapContentInfoToAll();
        } else {
            throw new IllegalStateException("no such map '" + mapName + "'");
        }
    }

    private MapContentInfoDto createServerContentDto(String mapName) {
        ServerContentManager.Map map = MODULE_CONTENT_MANAGER.getMaps()
                .stream()
                .filter(m -> m.name().equals(mapName))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("no such map on disk: " + mapName));

        var builder =
                MapContentInfoDto.builder()
                        .name(map.name())
                        .filename(map.filename());

        Set<MapContentInfoDto.Mapkit> mapkits = new HashSet<>();

        map.mapkits().forEach(
                mk -> mapkits.add(MapContentInfoDto.Mapkit.builder()
                        .name(mk.name())
                        .dirname(mk.dirname())
                        .files(Set.copyOf(mk.files()))
                        .build())
        );

        var builtInMapkit = MapContentInfoDto.Mapkit.builder()
                .name(BuiltInMapkit.NAME)
                .dirname(BuiltInMapkit.NAME)
                .files(MODULE_CONTENT_MANAGER.getMapkits()
                        .stream()
                        .filter(m -> m.name().equals(BuiltInMapkit.NAME))
                        .findAny()
                        .orElseThrow()
                        .files()
                )
                .build();

        mapkits.add(builtInMapkit);

        builder.mapkits(mapkits);

        Set<MapContentInfoDto.Character> characters = new HashSet<>();

        characters.add(MapContentInfoDto.Character.builder()
                .mapkitName("builtin-mapkit")
                .mapkitItemId("character_blake")
                .build()
        );
        characters.add(MapContentInfoDto.Character.builder()
                .mapkitName("builtin-mapkit")
                .mapkitItemId("character_ava")
                .build()
        );

        builder.characters(characters);

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
            SERVER_WORLD_SCENE.removePlayer(player);
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























