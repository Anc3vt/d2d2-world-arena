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
import ru.ancevt.d2d2world.net.message.Message;
import ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl;
import ru.ancevt.d2d2world.net.protocol.ClientProtocolImplListener;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import ru.ancevt.net.messaging.CloseStatus;
import ru.ancevt.net.messaging.connection.ConnectionFactory;
import ru.ancevt.net.messaging.connection.ConnectionListener;
import ru.ancevt.net.messaging.connection.IConnection;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerControllerAndXYReport;
import static ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerEnterRequest;
import static ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerExitRequest;
import static ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerPingReport;
import static ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerPingRequest;
import static ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessagePlayerTextToChat;
import static ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessageRconCommand;
import static ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessageRconLogin;
import static ru.ancevt.d2d2world.net.protocol.ProtocolImpl.PROTOCOL_VERSION;

@Slf4j
public class Client implements ConnectionListener, ClientProtocolImplListener {

    private IConnection connection;
    private final ClientProtocolImpl protocolImpl;
    private final RemotePlayerManager remotePlayerManager;
    private final List<ClientListener> clientListeners;
    private ClientSender sender;
    private long pingRequestTime;
    private String localPlayerName;
    private String serverProtocolVersion;
    private int ping;
    private int localPlayerId;
    private int localPlayerColor;

    public Client() {
        remotePlayerManager = RemotePlayerManager.INSTANCE;
        clientListeners = new CopyOnWriteArrayList<>();

        protocolImpl = new ClientProtocolImpl();
        protocolImpl.addClientProtocolImplListener(this);
    }

    @Override
    public void connectionEstablished() {
        clientListeners.forEach(ClientListener::clientConnectionEstablished);
    }

    /**
     * {@link ConnectionListener} method
     */
    @Override
    public void connectionBytesReceived(byte[] bytes) {
        protocolImpl.bytesReceived(bytes);
    }

    /**
     * {@link ConnectionListener} method
     */
    @Override
    public void connectionClosed(@NotNull CloseStatus status) {
        clientListeners.forEach(l -> l.clientConnectionClosed(status));
    }

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
        throw new NotImplementedException();
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

        remotePlayerManager.getRemotePlayer(remotePlayerId).ifPresentOrElse(
                // if present
                remotePlayer -> {
                    remotePlayer.update(remotePlayerName, remotePlayerColor);
                    clientListeners.forEach(l -> l.remotePlayerIntroduce(remotePlayer));
                },

                // or else
                () -> {
                    RemotePlayer remotePlayer = remotePlayerManager.createRemotePlayer(
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
    public void remotePlayerControllerAndXY(int remotePlayerId,
                                            int remotePlayerControllerState,
                                            float remotePlayerX,
                                            float remotePlayerY) {

        remotePlayerManager.getRemotePlayer(remotePlayerId).ifPresent(remotePlayer -> {
            remotePlayer.setControllerState(remotePlayerControllerState);
            remotePlayer.setXY(remotePlayerX, remotePlayerY);
        });
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void remotePlayerExit(int remotePlayerId, int remotePlayerExitCause) {
        remotePlayerManager.removeRemotePlayer(remotePlayerId).ifPresent(
                remotePlayer -> clientListeners.forEach(l -> l.remotePlayerExit(remotePlayer))
        );
    }

    /**
     * {@link ClientProtocolImplListener} method
     */

    @Override
    public void extraFromServer(@NotNull String extraDataFromServer) {
        throw new NotImplementedException();
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void errorFromServer(int errorCode, @NotNull String errorMessage, String errorDetails) {
        throw new NotImplementedException();
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void playerPingResponse() {
        long pingResponseTime = System.currentTimeMillis();
        ping = (int) (pingResponseTime - pingRequestTime);
        sender.send(createMessagePlayerPingReport(ping));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void remotePlayerPing(int remotePlayerId, int remotePlayerPing) {
        remotePlayerManager.getRemotePlayer(remotePlayerId).ifPresent(
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
    public void serverInfoResponse(@NotNull ServerInfoRetrieveResult result) {
        clientListeners.forEach(l -> l.serverInfo(result));
    }

    /**
     * {@link ClientProtocolImplListener} method
     */
    @Override
    public void serverTextToPlayer(@NotNull String text, int textColor) {
        clientListeners.forEach(l -> l.serverTextToPlayer(text, textColor));
    }

    public void sendPlayerEnterRequest() {
        sender.send(createMessagePlayerEnterRequest(localPlayerName, PROTOCOL_VERSION, "")
        );
    }

    public void sendLocalPlayerControllerAndXYReport(int controllerState, float x, float y) {
        sender.send(createMessagePlayerControllerAndXYReport(controllerState, x, y));
    }

    public boolean isEnteredServer() {
        return localPlayerName != null;
    }

    public void pingRequest() {
        pingRequestTime = System.currentTimeMillis();
        sender.send(createMessagePlayerPingRequest());
    }

    public void connect(String host, int port) {
        if (connection != null) {
            if (connection.isOpen()) {
                connection.close();
            }
            connection.removeConnectionListener(this);
        }

        connection = ConnectionFactory.createTcpB254Connection();
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

    public RemotePlayerManager getRemotePlayerManager() {
        return remotePlayerManager;
    }

    public long getPing() {
        return ping;
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

    private static byte[] debugReceived(@NotNull String playerNameOrId, byte[] bytes) {
        log.debug("Player {} received {}", playerNameOrId, Message.debug(bytes));
        return bytes;
    }

    private static byte[] debugSent(@NotNull String playerNameOrId, byte[] bytes) {
        log.debug("Player {} sent {}", playerNameOrId, Message.debug(bytes));
        return bytes;
    }

    public void sendPlayerControllerAndXY(int controllerState, float x, float y) {
        connection.send(ServerProtocolImpl.createMessageRemotePlayerControllerAndXY(localPlayerId, controllerState, x, y));
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
}






























