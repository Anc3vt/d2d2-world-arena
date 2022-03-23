/*
 *   D2D2 World Arena Networking
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
package com.ancevt.d2d2world.net.client;

import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2world.data.file.FileDataUtils;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.dto.PlayerDto;
import com.ancevt.d2d2world.net.dto.client.*;
import com.ancevt.d2d2world.net.dto.server.*;
import com.ancevt.d2d2world.net.protocol.ClientProtocolImpl;
import com.ancevt.d2d2world.net.protocol.ClientProtocolImplListener;
import com.ancevt.d2d2world.net.transfer.FileReceiver;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.d2d2world.net.transfer.Headers;
import com.ancevt.d2d2world.sync.ISyncDataReceiver;
import com.ancevt.d2d2world.sync.SyncDataReceiver;
import com.ancevt.net.CloseStatus;
import com.ancevt.net.TcpFactory;
import com.ancevt.net.connection.ConnectionListener;
import com.ancevt.net.connection.IConnection;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ancevt.d2d2world.net.client.PlayerManager.PLAYER_MANAGER;
import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.*;
import static com.ancevt.d2d2world.net.protocol.ProtocolImpl.PROTOCOL_VERSION;
import static com.ancevt.d2d2world.net.transfer.Headers.*;

@Slf4j
public class Client implements ConnectionListener, ClientProtocolImplListener {

    public static final Client MODULE_CLIENT = new Client();

    private IConnection connection;
    private final List<ClientListener> clientListeners;
    private ClientSender sender;
    private String serverProtocolVersion;
    private long pingRequestTime;
    private int localPlayerId;
    private String localPlayerName;
    private int localPlayerFrags;
    private int localPlayerPing;
    private int localPlayerColor;
    private final ISyncDataReceiver syncDataReceiver;

    private Client() {
        this.syncDataReceiver = new SyncDataReceiver();
        clientListeners = new CopyOnWriteArrayList<>();

        MODULE_CLIENT_PROTOCOL.addClientProtocolImplListener(this);
    }

    public ISyncDataReceiver getSyncDataReceiver() {
        return syncDataReceiver;
    }

    // CONNECTION LISTENERS:

    /**
     * {@link ConnectionListener} method
     */
    @Override
    public void connectionEstablished() {
        clientListeners.forEach(ClientListener::clientConnectionEstablished);
    }

    /**
     * {@link ConnectionListener} method
     */
    @Override
    public void connectionBytesReceived(byte[] bytes) {
        MODULE_CLIENT_PROTOCOL.bytesReceived(bytes);
    }

    /**
     * {@link ConnectionListener} method
     */
    @Override
    public void connectionClosed(@NotNull CloseStatus status) {
        clientListeners.forEach(l -> l.clientConnectionClosed(status));
    }

    // CLIENT PROTOCOL LISTENERS:

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void dtoFromServer(@NotNull Dto dto) {
        log.info("extraFromServer: {}", dto);

        // TODO: extract to separate handler, maybe it should be a class
        if (dto instanceof ServerContentInfoDto d) {

            Queue<String> queue = new ConcurrentLinkedDeque<>();

            queue.add(sendFileRequest("data/maps/" + d.getFilename()));
            d.getMapkits().forEach(
                    mk -> mk.getFiles()
                            .forEach(
                                    filename -> queue.add(sendFileRequest("data/mapkits/" + mk.getUid() + "/" + filename))
                            )
            );
            FileReceiverManager.INSTANCE.addFileReceiverManagerListener(new FileReceiverManager.FileReceiverManagerListener() {
                @Override
                public void progress(FileReceiver fileReceiver) {

                }

                @Override
                public void complete(FileReceiver fileReceiver) {
                    queue.remove(fileReceiver.getPath());
                    if (queue.isEmpty()) {
                        clientListeners.forEach(l -> l.mapContentLoaded(d.getFilename()));
                        FileReceiverManager.INSTANCE.removeFileReceiverManagerListener(this);
                    }
                }
            });
        } else if (dto instanceof PlayerActorDto d) {
            clientListeners.forEach(l -> l.localPlayerActorGameObjectId(d.getPlayerActorGameObjectId()));
        } else if (dto instanceof PlayerEnterDto d) {
            PlayerDto playerDto = d.getPlayer();
            PLAYER_MANAGER.getPlayer(playerDto.getId()).ifPresent(
                    remotePlayer -> remotePlayer.update(playerDto.getName(), playerDto.getColor())
            );
            clientListeners.forEach(l -> l.playerEnterServer(playerDto.getId(), playerDto.getName(), playerDto.getColor()));
        } else if (dto instanceof RconResponseDto d) {
            clientListeners.forEach(l -> l.rconResponse(d.getText()));
        } else if (dto instanceof PlayerEnterResponseDto d) {
            PlayerDto playerDto = d.getPlayer();
            setLocalPlayerId(playerDto.getId());
            setLocalPlayerColor(playerDto.getColor());
            setServerProtocolVersion(d.getProtocolVersion());
            sendPingRequest();
            clientListeners.forEach(l -> l.playerEnterServer(localPlayerId, localPlayerColor, serverProtocolVersion, d.getServerStartTime()));
        } else if (dto instanceof PlayerExitDto d) {
            PLAYER_MANAGER.removePlayer(d.getPlayer().getId()).ifPresent(
                    remotePlayer -> clientListeners.forEach(l -> l.playerExit(remotePlayer))
            );
        } else if (dto instanceof ChatDto d) {
            d.getMessages().forEach(m -> {
                if (m.getPlayer() != null && m.getPlayer().getName() != null) {
                    PlayerDto p = m.getPlayer();
                    clientListeners.forEach(l -> l.playerChat(m.getId(), p.getId(), p.getName(), p.getColor(), m.getText(), m.getTextColor()));
                } else {
                    clientListeners.forEach(l -> l.serverChat(m.getId(), m.getText(), m.getTextColor()));
                }
            });
        } else if (dto instanceof ServerInfoDto d) {
            Set<PlayerDto> playerDtoSet = d.getPlayers();
            playerDtoSet.forEach(p -> {
                Player player = PLAYER_MANAGER.registerPlayer(p.getId(), p.getName(), p.getColor());
                player.setPing(p.getPing());
                player.setFrags(p.getFrags());
            });

            clientListeners.forEach(l -> l.serverInfo(d));
        } else if (dto instanceof ServerTextDto d) {
            clientListeners.forEach(l -> l.serverTextToPlayer(d.getText(), d.getColor()));
        } else if (dto instanceof DeathDto d) {
            clientListeners.forEach(l -> l.playerDeath(d.getDeadPlayerId(), d.getKillerPlayerId()));
        } else if (dto instanceof PlayerChatEventDto d) {
            int playerId = d.getPlayerId();
            String action = d.getAction();
            clientListeners.forEach(l->l.playerChatEvent(playerId, action));
        }
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void playerPingResponse() {
        long pingResponseTime = System.currentTimeMillis();
        localPlayerPing = (int) (pingResponseTime - pingRequestTime);
        sender.send(PlayerPingReportDto.builder().ping(localPlayerPing).build());
        PLAYER_MANAGER.getPlayer(localPlayerId).ifPresent(player -> player.setPing(localPlayerPing));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void fileData(@NotNull String headers, byte[] fileData) {
        clientListeners.forEach(l -> l.fileData(headers, fileData));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void serverSyncData(byte @NotNull [] syncData) {
        syncDataReceiver.bytesReceived(syncData);
    }

    // SENDERS:
    public void sendPlayerEnterRequest() {
        sender.send(PlayerEnterRequestDto.builder()
                .name(localPlayerName)
                .protocolVersion(PROTOCOL_VERSION)
                .build()
        );
    }

    public void sendAimXY(float aimX, float aimY) {
        sender.send(ClientProtocolImpl.createMessagePlayerAimXY(aimX, aimY));
    }

    public void sendLocalPlayerController(int controllerState) {
        sender.send(createMessagePlayerController(controllerState));
    }

    public void sendServerInfoRequest() {
        sender.send(ServerInfoRequestDto.INSTANCE);
    }

    public void sendPingRequest() {
        pingRequestTime = System.currentTimeMillis();
        sender.send(createMessagePing());
    }

    public String sendFileRequest(@NotNull String path) {
        var h = newHeaders();

        if (FileDataUtils.exists(path)) {
            h.put(HASH, MD5.hashFile(path));
        }

        return sendFileRequest(h.put(PATH, path));
    }

    public String sendFileRequest(@NotNull Headers headers) {
        sender.send(createMessageFileRequest(headers.toString()));
        return headers.get(PATH);
    }

    public void sendDto(Dto dto) {
        sender.send(dto);
    }

    public void sendChatMessage(String text) {
        sender.send(PlayerTextToChatDto.builder()
                .text(text)
                .build()
        );
    }

    public void sendExitRequest() {
        sender.send(PlayerExitRequestDto.INSTANCE);
    }

    public void sendRconLoginRequest(String passwordHash) {
        sender.send(RconLoginRequestDto.builder()
                .passwordHash(passwordHash)
                .build()
        );
    }

    public void sendRconCommand(String rconCommandText) {
        sender.send(RconCommandDto.builder()
                .commandText(rconCommandText)
                .build()
        );
    }

    ///

    public boolean isEnteredServer() {
        return localPlayerName != null;
    }

    public void connect(String host, int port) {
        if (connection != null) {
            connection.closeIfOpen();
            connection.removeConnectionListener(this);
        }

        connection = TcpFactory.createConnection(0);
        log.info("connecting... Connection object: {}", connection);
        sender = new ClientSender(connection);
        connection.addConnectionListener(this);
        connection.asyncConnect(host, port);
    }

    public void addClientListener(ClientListener l) {
        clientListeners.add(l);
    }

    public void removeClientListener(ClientListener l) {
        clientListeners.remove(l);
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public void setLocalPlayerName(@NotNull String playerName) {
        this.localPlayerName = playerName;
    }

    public int getLocalPlayerId() {
        return localPlayerId;
    }

    public void setLocalPlayerId(int playerId) {
        this.localPlayerId = playerId;
    }

    public int getLocalPlayerColor() {
        return localPlayerColor;
    }

    public void setLocalPlayerColor(int color) {
        this.localPlayerColor = color;
    }

    public int getLocalPlayerPing() {
        return localPlayerPing;
    }

    public void setLocalPlayerFrags(int localPlayerFrags) {
        this.localPlayerFrags = localPlayerFrags;
    }

    public int getLocalPlayerFrags() {
        return localPlayerFrags;
    }

    public void close() {
        connection.close();
    }

    public void setServerProtocolVersion(@NotNull String serverProtocolVersion) {
        this.serverProtocolVersion = serverProtocolVersion;
    }

    public String getServerProtocolVersion() {
        return serverProtocolVersion;
    }

    public boolean isConnected() {
        return connection != null && connection.isOpen();
    }

    public IConnection getConnection() {
        return connection;
    }

}


