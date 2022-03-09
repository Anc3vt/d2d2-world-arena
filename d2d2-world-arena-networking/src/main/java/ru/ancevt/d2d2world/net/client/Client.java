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
package ru.ancevt.d2d2world.net.client;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.exception.NotImplementedException;
import ru.ancevt.commons.hash.MD5;
import ru.ancevt.d2d2world.data.file.FileDataUtils;
import ru.ancevt.d2d2world.net.dto.ExtraDto;
import ru.ancevt.d2d2world.net.dto.PlayerActorDto;
import ru.ancevt.d2d2world.net.dto.ServerMapInfoDto;
import ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl;
import ru.ancevt.d2d2world.net.protocol.ClientProtocolImplListener;
import ru.ancevt.d2d2world.net.transfer.FileReceiver;
import ru.ancevt.d2d2world.net.transfer.FileReceiverManager;
import ru.ancevt.d2d2world.net.transfer.Headers;
import ru.ancevt.d2d2world.sync.ISyncDataReceiver;
import ru.ancevt.d2d2world.sync.SyncDataReceiver;
import ru.ancevt.net.tcpb254.CloseStatus;
import ru.ancevt.net.tcpb254.connection.ConnectionFactory;
import ru.ancevt.net.tcpb254.connection.ConnectionListener;
import ru.ancevt.net.tcpb254.connection.IConnection;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.ancevt.d2d2world.net.client.RemotePlayerManager.PLAYER_MANAGER;
import static ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl.*;
import static ru.ancevt.d2d2world.net.protocol.ProtocolImpl.PROTOCOL_VERSION;
import static ru.ancevt.d2d2world.net.transfer.Headers.*;

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
    public void remotePlayerEnter(int remotePlayerId, @NotNull String remotePlayerName, int remotePlayerColor) {
        clientListeners.forEach(l -> l.remotePlayerEnterServer(remotePlayerId, remotePlayerName, remotePlayerColor));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void rconResponse(@NotNull String rconResponseData) {
        clientListeners.forEach(l -> l.rconResponse(rconResponseData));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void playerEnterResponse(int localPlayerId, int localPlayerColor, @NotNull String serverProtocolVersion) {
        setLocalPlayerId(localPlayerId);
        setLocalPlayerColor(localPlayerColor);
        setServerProtocolVersion(serverProtocolVersion);
        clientListeners.forEach(l -> l.playerEnterServer(localPlayerId, localPlayerColor, serverProtocolVersion));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void remotePlayerIntroduce(int remotePlayerId,
                                      @NotNull String remotePlayerName,
                                      int remotePlayerColor,
                                      @NotNull String remotePlayerExtraData) {

        PLAYER_MANAGER.getRemotePlayer(remotePlayerId).ifPresentOrElse(
                // if present
                remotePlayer -> {
                    remotePlayer.update(remotePlayerName, remotePlayerColor);
                    clientListeners.forEach(l -> l.remotePlayerIntroduce(remotePlayer));
                },

                // or else
                () -> {
                    RemotePlayer remotePlayer = PLAYER_MANAGER.createRemotePlayer(
                            remotePlayerId,
                            remotePlayerName,
                            remotePlayerColor
                    );

                    clientListeners.forEach(l -> l.remotePlayerIntroduce(remotePlayer));
                });
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void remotePlayerExit(int remotePlayerId, int remotePlayerExitCause) {
        PLAYER_MANAGER.removeRemotePlayer(remotePlayerId).ifPresent(
                remotePlayer -> clientListeners.forEach(l -> l.remotePlayerExit(remotePlayer))
        );
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void extraFromServer(@NotNull ExtraDto extraDto) {
        log.info("extraFromServer: {}", extraDto);


        // TODO: extract to separate handler, maybe it should be a class
        if (extraDto instanceof ServerMapInfoDto dto) {

            Queue<String> queue = new ConcurrentLinkedDeque<>();

            queue.add(sendFileRequest("data/maps/" + dto.getFilename()));
            dto.getMapkits().forEach(
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
                        clientListeners.forEach(l -> l.mapContentLoaded(dto.getFilename()));
                        FileReceiverManager.INSTANCE.removeFileReceiverManagerListener(this);
                    }
                }
            });
        } else if (extraDto instanceof PlayerActorDto dto) {
            clientListeners.forEach(l->l.localPlayerActorGameObjectId(dto.getPlayerActorGameObjectId()));
        }

    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void errorFromServer(int errorCode, @NotNull String errorMessage, @NotNull String errorDetails) {
        throw new NotImplementedException();
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void playerPingResponse() {
        long pingResponseTime = System.currentTimeMillis();
        localPlayerPing = (int) (pingResponseTime - pingRequestTime);
        sender.send(createMessagePlayerPingReport(localPlayerPing));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void remotePlayerPing(int remotePlayerId, int remotePlayerPing) {
        PLAYER_MANAGER.getRemotePlayer(remotePlayerId).ifPresent(
                remotePlayer -> remotePlayer.setPing(remotePlayerPing)
        );
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void serverChat(int chatMessageId, @NotNull String chatMessageText, int chatMessageTextColor) {
        clientListeners.forEach(l -> l.serverChat(chatMessageId, chatMessageText, chatMessageTextColor));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void playerChat(int chatMessageId,
                           int playerId,
                           @NotNull String playerName,
                           int playerColor,
                           @NotNull String chatMessageText,
                           int chatMessageTextColor) {

        clientListeners.forEach(l -> l.playerChat(
                chatMessageId, playerId, playerName, playerColor, chatMessageText, chatMessageTextColor));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void serverInfoResponse(@NotNull ServerInfo result) {
        localPlayerPing = (int) (System.currentTimeMillis() - pingRequestTime);
        clientListeners.forEach(l -> l.serverInfo(result));
        sender.send(ClientProtocolImpl.createMessagePlayerPingReport(localPlayerPing));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void serverTextToPlayer(@NotNull String text, int textColor) {
        clientListeners.forEach(l -> l.serverTextToPlayer(text, textColor));
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
        sender.send(createMessagePlayerEnterRequest(localPlayerName, PROTOCOL_VERSION, ""));
    }

    public void sendLocalPlayerController(int controllerState) {
        sender.send(createMessagePlayerController(controllerState));
    }

    public void sendServerInfoRequest() {
        pingRequestTime = System.currentTimeMillis();
        sender.send(createMessageServerInfoRequest());
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

    public void sendExtra(ExtraDto dto) {
        sender.send(createMessageExtra(dto));
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

        connection = ConnectionFactory.createTcpB254Connection();
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

    public void sendChatMessage(String text) {
        sender.send(createMessagePlayerTextToChat(text));
    }

    public void sendExitRequest() {
        sender.send(createMessagePlayerExitRequest());
    }

    public void sendRconLoginRequest(String passwordHash) {
        sender.send(createMessageRconLogin(passwordHash));
    }

    public void sendRconCommand(String rconCommandText) {
        sender.send(createMessageRconCommand(rconCommandText, ""));
    }

    public IConnection getConnection() {
        return connection;
    }
}


