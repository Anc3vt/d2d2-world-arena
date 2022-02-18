/*
 *   D2D2 World Client
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
import ru.ancevt.commons.Pair;
import ru.ancevt.commons.io.ByteInputReader;
import ru.ancevt.commons.io.ByteOutputWriter;
import ru.ancevt.d2d2world.net.message.Message;
import ru.ancevt.d2d2world.net.message.MessageType;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class ServerProtocolImpl extends ProtocolImpl {

    private final List<ServerProtocolImplListener> serverProtocolImplListeners;

    public ServerProtocolImpl() {
        this.serverProtocolImplListeners = new ArrayList<>();
    }

    public void addServerProtocolImplListener(@NotNull ServerProtocolImplListener l) {
        serverProtocolImplListeners.add(l);
    }

    public void removeServerProtocolImplListener(@NotNull ServerProtocolImplListener l) {
        serverProtocolImplListeners.remove(l);
    }

    public void bytesReceived(int connectionId, byte[] bytes) {
        Message message = Message.of(bytes);
        ByteInputReader in = message.inputReader();

        switch (message.getType()) {

            case MessageType.CLIENT_SERVER_INFO_REQUEST -> {
                log.debug("received CLIENT_SERVER_INFO_REQUEST");
                serverProtocolImplListeners.forEach(l -> l.serverInfoRequest(connectionId));
            }

            case MessageType.CLIENT_PLAYER_ENTER_REQUEST -> {
                log.debug("received CLIENT_PLAYER_ENTER_REQUEST");
                String name = in.readUtf(byte.class);
                String clientProtocolVersion = in.readUtf(byte.class);
                String extraData = in.hasNextData() ? in.readUtf(int.class) : null;

                serverProtocolImplListeners.forEach(l ->
                        l.playerEnterRequest(connectionId, name, clientProtocolVersion, extraData));
            }

            case MessageType.CLIENT_PLAYER_EXIT_REQUEST -> {
                log.debug("received CLIENT_PLAYER_EXIT_REQUEST");
                serverProtocolImplListeners.forEach(l -> l.playerExitRequest(connectionId));
            }

            case MessageType.CLIENT_PLAYER_CONTROLLER_AND_XY_REPORT -> {
                int controlState = in.readByte();
                float x = in.readFloat();
                float y = in.readFloat();
                serverProtocolImplListeners.forEach(l -> l.playerControllerAndXYReport(connectionId, controlState, x, y));
            }

            case MessageType.CLIENT_PLAYER_TEXT_TO_CHAT -> {
                log.debug("received CLIENT_PLAYER_TEXT_TO_CHAT");
                String text = in.readUtf(byte.class);
                serverProtocolImplListeners.forEach(l -> l.playerTextToChat(connectionId, text));
            }

            case MessageType.CLIENT_RCON_LOGIN -> {
                log.debug("received CLIENT_RCON_LOGIN");
                String login = in.readUtf(byte.class);
                String passwordHash = in.readUtf(byte.class);
                serverProtocolImplListeners.forEach(l -> l.rconLogin(connectionId, login, passwordHash));
            }

            case MessageType.CLIENT_RCON_COMMAND -> {
                log.debug("received CLIENT_RCON_COMMAND");
                String commandText = in.readUtf(byte.class);
                String extraData = in.hasNextData() ? in.readUtf(int.class) : "";
                serverProtocolImplListeners.forEach(l -> l.rconCommand(connectionId, commandText, extraData));
            }

            case MessageType.CLIENT_PLAYER_PING_REQUEST -> {
                log.debug("received CLIENT_PLAYER_PING_REQUEST");
                serverProtocolImplListeners.forEach(l -> l.playerPingRequest(connectionId));
            }

            case MessageType.CLIENT_PLAYER_PING_REPORT -> {
                log.debug("received CLIENT_PLAYER_PING_REPORT");
                int ping = in.readShort();
                serverProtocolImplListeners.forEach(l -> l.playerPingReport(connectionId, ping));
            }

            case MessageType.EXTRA -> {
                log.debug("received EXTRA");
                String extraDataFromPlayer = in.readUtf(int.class);
                serverProtocolImplListeners.forEach(l -> l.extraFromPlayer(connectionId, extraDataFromPlayer));
            }

            case MessageType.ERROR -> {
                log.debug("received ERROR");
                int errorCode = in.readShort();
                String errorMessage = in.readUtf(byte.class);
                String errorDetails = in.readUtf(int.class);
                serverProtocolImplListeners.forEach(l -> l.errorFromPlayer(errorCode, errorMessage, errorDetails));
            }
        }
    }

    public static byte[] createMessageServerInfoResponse(@NotNull String serverName,
                                                         @NotNull String serverVersion,
                                                         @NotNull String mapName,
                                                         @NotNull String mapkitName,
                                                         @NotNull String modName,
                                                         List<Pair<Integer, String>> players) {
        ByteOutputWriter bow = ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_INFO_RESPONSE)
                .writeUtf(byte.class, serverName)
                .writeUtf(byte.class, serverVersion)
                .writeUtf(byte.class, PROTOCOL_VERSION)
                .writeUtf(byte.class, mapName)
                .writeUtf(byte.class, mapkitName)
                .writeUtf(byte.class, modName);

        players.forEach(p -> bow.writeShort(p.getFirst()).writeUtf(byte.class, p.getSecond()));

        return bow.toArray();
    }

    public static byte[] createMessageRconResponse(String responseData) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_RCON_RESPONSE)
                .writeUtf(int.class, responseData)
                .toArray();
    }

    public static byte[] createMessageRemotePlayerEnter(int playerId,
                                                        @NotNull String playerName,
                                                        int playerColor) {

        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_REMOTE_PLAYER_ENTER)
                .writeShort(playerId)
                .writeUtf(byte.class, playerName)
                .writeInt(playerColor)
                .toArray();
    }

    public static byte[] createMessagePlayerEnterResponse(int playerId, int color) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_PLAYER_ENTER_RESPONSE)
                .writeShort(playerId)
                .writeInt(color)
                .writeUtf(byte.class, PROTOCOL_VERSION)
                .toArray();
    }

    public static byte[] createMessageRemotePlayerIntroduce(int playerId,
                                                            @NotNull String playerName,
                                                            int color,
                                                            @NotNull String extraData) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_REMOTE_PLAYER_INTRODUCE)
                .writeShort(playerId)
                .writeUtf(byte.class, playerName)
                .writeInt(color)
                .writeUtf(int.class, extraData)
                .toArray();
    }

    public static byte[] createMessageChat(int chatMessageId, @NotNull String text) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_CHAT)
                .writeInt(chatMessageId)
                .writeUtf(byte.class, text)
                .toArray();

    }

    public static byte[] createMessageChat(int chatMessageId,
                                           @NotNull String text,
                                           int playerId,
                                           @NotNull String playerName,
                                           int playerColor) {

        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_CHAT)
                .writeInt(chatMessageId)
                .writeUtf(byte.class, text)
                .writeShort(playerId)
                .writeUtf(byte.class, playerName)
                .writeInt(playerColor)
                .toArray();

    }

    public static byte[] createMessageRemotePlayerControllerAndXY(int playerId, int controllerState, float x, float y) {
        return ByteOutputWriter.newInstance(12)
                .writeByte(MessageType.SERVER_REMOTE_PLAYER_CONTROLLER_AND_XY)
                .writeShort(playerId)
                .writeByte(controllerState)
                .writeFloat(x)
                .writeFloat(y)
                .toArray();
    }

    public static byte[] createMessageRemotePlayerExit(int playerId, @NotNull ExitCause exitReason) {
        return ByteOutputWriter.newInstance(4)
                .writeByte(MessageType.SERVER_REMOTE_PLAYER_EXIT)
                .writeShort(playerId)
                .writeByte(exitReason.getValue())
                .toArray();
    }

    public static byte[] createMessagePlayerPingResponse() {
        return new byte[]{MessageType.SERVER_PLAYER_PING_RESPONSE};
    }

    public static byte[] createMessageRemotePlayerPingValue(int playerId, int pingValue) {
        return ByteOutputWriter.newInstance(5)
                .writeByte(MessageType.SERVER_REMOTE_PLAYER_PING_VALUE)
                .writeShort(playerId)
                .writeShort(pingValue)
                .toArray();
    }
}






































