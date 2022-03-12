package ru.ancevt.d2d2world.net.client;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import ru.ancevt.net.tcpb254.CloseStatus;

import java.time.LocalDateTime;

public class ClientListenerAdapter implements ClientListener{
    @Override
    public void remotePlayerIntroduce(@NotNull Player remotePlayer) {

    }

    @Override
    public void remotePlayerExit(@NotNull Player remotePlayer) {

    }

    @Override
    public void playerEnterServer(int localPlayerId,
                                  int localPlayerColor,
                                  @NotNull String serverProtocolVersion,
                                  @NotNull LocalDateTime serverStartTime) {

    }

    @Override
    public void serverChat(int chatMessageId, @NotNull String chatMessageText, int chatMessageTextColor) {

    }

    @Override
    public void playerChat(int chatMessageId,
                           int playerId,
                           @NotNull String playerName,
                           int playerColor,
                           @NotNull String chatMessageText,
                           int textColor) {

    }

    @Override
    public void clientConnectionClosed(@NotNull CloseStatus status) {

    }

    @Override
    public void clientConnectionEstablished() {

    }

    @Override
    public void remotePlayerEnterServer(int remotePlayerId, @NotNull String remotePlayerName, int remotePlayerColor) {

    }

    @Override
    public void serverInfo(@NotNull ServerInfoDto result) {

    }

    @Override
    public void serverTextToPlayer(@NotNull String text, int textColor) {

    }

    @Override
    public void fileData(@NotNull String headers, byte[] fileData) {

    }

    @Override
    public void rconResponse(String rconResponseData) {

    }

    @Override
    public void mapContentLoaded(String mapFilename) {

    }

    @Override
    public void localPlayerActorGameObjectId(int playerActorGameObjectId) {

    }
}
