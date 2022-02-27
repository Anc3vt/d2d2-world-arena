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
package ru.ancevt.d2d2world.net.protocol;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.io.ByteInputReader;
import ru.ancevt.commons.io.ByteOutputWriter;
import ru.ancevt.d2d2world.net.client.RemotePlayer;
import ru.ancevt.d2d2world.net.client.ServerInfo;
import ru.ancevt.d2d2world.net.message.Message;
import ru.ancevt.d2d2world.net.message.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public final class ClientProtocolImpl extends ProtocolImpl {

    private final List<ClientProtocolImplListener> clientProtocolImplListeners;

    public ClientProtocolImpl() {
        this.clientProtocolImplListeners = new CopyOnWriteArrayList<>();
    }

    public void addClientProtocolImplListener(@NotNull ClientProtocolImplListener l) {
        clientProtocolImplListeners.add(l);
    }

    public void removeClientProtocolImplListener(@NotNull ClientProtocolImplListener l) {
        clientProtocolImplListeners.remove(l);
    }

    public void bytesReceived(byte[] bytes) {
        Message message = Message.of(bytes);
        ByteInputReader in = message.inputReader();

        try {

            switch (message.getType()) {

                case MessageType.SERVER_INFO_RESPONSE -> {
                    log("received SERVER_INFO_RESPONSE");
                    ServerInfo result = readServerInfoResponseBytes(bytes);
                    clientProtocolImplListeners.forEach(l -> l.serverInfoResponse(result));
                }

                case MessageType.SERVER_RCON_RESPONSE -> {
                    log("received SERVER_RCON_RESPONSE");
                    String rconResponseData = in.readUtf(int.class);
                    clientProtocolImplListeners.forEach(l -> l.rconResponse(rconResponseData));
                }

                case MessageType.SERVER_REMOTE_PLAYER_ENTER -> {
                    log("received SERVER_REMOTE_PLAYER_ENTER");
                    int remotePlayerId = in.readShort();
                    String remotePlayerName = in.readUtf(byte.class);
                    int remotePlayerColor = in.readInt();
                    clientProtocolImplListeners.forEach(
                            l -> l.remotePlayerEnter(remotePlayerId, remotePlayerName, remotePlayerColor));
                }

                case MessageType.SERVER_PLAYER_ENTER_RESPONSE -> {
                    log("received SERVER_PLAYER_ENTER_RESPONSE");
                    int playerId = in.readShort();
                    int color = in.readInt();
                    String serverProtocolVersion = in.readUtf(byte.class);
                    clientProtocolImplListeners.forEach(l ->
                            l.playerEnterResponse(playerId, color, serverProtocolVersion));
                }

                case MessageType.SERVER_REMOTE_PLAYER_INTRODUCE -> {
                    log("received SERVER_REMOTE_PLAYER_INTRODUCE");
                    int remotePlayerId = in.readShort();
                    String remotePlayerName = in.readUtf(byte.class);
                    int remotePlayerColor = in.readInt();
                    String remotePlayerExtraData = in.readUtf(int.class);
                    clientProtocolImplListeners.forEach(l -> l.remotePlayerIntroduce(
                            remotePlayerId, remotePlayerName, remotePlayerColor, remotePlayerExtraData));
                }

                case MessageType.SERVER_CHAT -> {
                    log("received SERVER_CHAT");
                    int chatMessageId = in.readInt();
                    String chatMessageText = in.readUtf(byte.class);
                    int chatMessageTextColor = in.readInt();

                    if (in.hasNextData()) {
                        int playerId = in.readShort();
                        String playerName = in.readUtf(byte.class);
                        int playerColor = in.readInt();
                        clientProtocolImplListeners.forEach(l -> l.playerChat(
                                chatMessageId,
                                playerId,
                                playerName,
                                playerColor,
                                chatMessageText,
                                chatMessageTextColor));

                    } else {
                        clientProtocolImplListeners.forEach(l -> l.serverChat(
                                chatMessageId, chatMessageText, chatMessageTextColor)
                        );
                    }
                }

                case MessageType.SERVER_TEXT_TO_PLAYER -> {
                    log("received SERVER_TEXT_TO_PLAYER");
                    int textColor = in.readInt();
                    String text = in.readUtf(byte.class);
                    clientProtocolImplListeners.forEach(l -> l.serverTextToPlayer(text, textColor));
                }

                case MessageType.SERVER_REMOTE_PLAYER_CONTROLLER_AND_XY -> {
                    int remotePlayerId = in.readShort();
                    int remotePlayerControllerState = in.readByte();
                    float remotePlayerX = in.readFloat();
                    float remotePlayerY = in.readFloat();

                    System.setProperty("debug", System.currentTimeMillis() + " " + remotePlayerX);

                    clientProtocolImplListeners.forEach(l -> l.remotePlayerControllerAndXY(
                            remotePlayerId, remotePlayerControllerState, remotePlayerX, remotePlayerY));
                }

                case MessageType.SERVER_REMOTE_PLAYER_EXIT -> {
                    log("received SERVER_REMOTE_PLAYER_EXIT");
                    int remotePlayerId = in.readShort();
                    int remotePlayerExitCause = in.readByte();
                    clientProtocolImplListeners.forEach(l -> l.remotePlayerExit(
                            remotePlayerId, remotePlayerExitCause));
                }

                case MessageType.SERVER_PLAYER_PING_RESPONSE -> {
                    log("received SERVER_PLAYER_PING_RESPONSE");
                    clientProtocolImplListeners.forEach(l -> l.playerPingResponse());
                }

                case MessageType.SERVER_REMOTE_PLAYER_PING_VALUE -> {
                    log("received SERVER_REMOTE_PLAYER_PING_VALUE");
                    int remotePlayerId = in.readShort();
                    int remotePlayerPing = in.readShort();
                    clientProtocolImplListeners.forEach(l -> l.remotePlayerPing(remotePlayerId, remotePlayerPing));
                }

                case MessageType.FILE_DATA -> {
                    log("received FILE_DATA");
                    String headers = in.readUtf(short.class);
                    int contentLength = in.readInt();
                    byte[] fileData = in.readBytes(contentLength);
                    clientProtocolImplListeners.forEach(l -> l.fileData(headers, fileData));
                }

                case MessageType.EXTRA -> {
                    log("received EXTRA");
                    String extraDataFromServer = in.readUtf(int.class);
                    clientProtocolImplListeners.forEach(l -> l.extraFromServer(extraDataFromServer));
                }

                case MessageType.ERROR -> {
                    log("received ERROR");
                    int errorCode = in.readShort();
                    String errorMessage = in.readUtf(byte.class);
                    String errorDetails = in.readUtf(int.class);
                    clientProtocolImplListeners.forEach(l -> l.errorFromServer(errorCode, errorMessage, errorDetails));
                }
            }

        } catch (Exception e) {
            log.error("message" + message.getBytes().length, e);
        }
    }

    private void log(Object o) {
        System.out.println(o);
    }

    public static ServerInfo readServerInfoResponseBytes(byte[] bytes) {
        Message message = Message.of(bytes);
        ByteInputReader in = message.inputReader();

        String serverName = in.readUtf(byte.class);
        String serverVersion = in.readUtf(byte.class);
        String serverProtocolVersion = in.readUtf(byte.class);
        String mapName = in.readUtf(byte.class);
        String mapkitName = in.readUtf(byte.class);
        String modName = in.readUtf(byte.class);
        int maxPlayers = in.readShort();

        List<RemotePlayer> players = new ArrayList<>();
        while (in.hasNextData())
            players.add(new RemotePlayer(in.readShort(), in.readUtf(byte.class), 0));

        return new ServerInfo(
                serverName,
                serverVersion,
                serverProtocolVersion,
                mapName,
                mapkitName,
                modName,
                maxPlayers,
                players
        );
    }

    public static byte[] createMessageServerInfoRequest() {
        return new byte[]{MessageType.CLIENT_SERVER_INFO_REQUEST};
    }

    public static byte[] createMessageRconLogin(@NotNull String passwordHash) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_RCON_LOGIN)
                .writeUtf(byte.class, passwordHash)
                .toArray();
    }

    public static byte[] createMessageRconCommand(@NotNull String commandText, @NotNull String extraData) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_RCON_COMMAND)
                .writeUtf(byte.class, commandText)
                .writeUtf(int.class, extraData)
                .toArray();
    }

    public static byte[] createMessagePlayerEnterRequest(@NotNull String playerName,
                                                         @NotNull String clientProtocolVersion,
                                                         @NotNull String extraData) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_ENTER_REQUEST)
                .writeUtf(byte.class, playerName)
                .writeUtf(byte.class, clientProtocolVersion)
                .writeUtf(int.class, extraData)
                .toArray();
    }

    public static byte[] createMessagePlayerExitRequest() {
        return new byte[]{MessageType.CLIENT_PLAYER_EXIT_REQUEST};
    }

    public static byte[] createMessagePlayerControllerAndXYReport(int controllerState, float x, float y) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_CONTROLLER_AND_XY_REPORT)
                .writeByte(controllerState)
                .writeFloat(x)
                .writeFloat(y)
                .toArray();
    }

    public static byte[] createMessagePlayerTextToChat(String chatMessageText) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_TEXT_TO_CHAT)
                .writeUtf(byte.class, chatMessageText)
                .toArray();
    }

    public static byte[] createMessageFileRequest(@NotNull String headers) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_REQUEST_FILE)
                .writeUtf(short.class, headers)
                .toArray();
    }

    public static byte[] createMessagePlayerPingRequest() {
        return new byte[]{MessageType.CLIENT_PLAYER_PING_REQUEST};
    }

    public static byte[] createMessagePlayerPingReport(int ping) {
        return ByteOutputWriter.newInstance(3)
                .writeByte(MessageType.CLIENT_PLAYER_PING_REPORT)
                .writeShort(ping)
                .toArray();
    }
}



























