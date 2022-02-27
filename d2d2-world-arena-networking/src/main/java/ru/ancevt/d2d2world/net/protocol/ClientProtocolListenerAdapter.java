package ru.ancevt.d2d2world.net.protocol;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.net.client.ServerInfo;

public class ClientProtocolListenerAdapter implements ClientProtocolImplListener {
    @Override
    public void rconResponse(@NotNull String rconResponseData) {

    }

    @Override
    public void playerEnterResponse(int playerId, int color, @NotNull String serverProtocolVersion) {

    }

    @Override
    public void remotePlayerEnter(int remotePlayerId, @NotNull String remotePlayerName, int remotePlayerColor) {

    }

    @Override
    public void remotePlayerIntroduce(int remotePlayerId, @NotNull String remotePlayerName, int remotePlayerColor, @NotNull String remotePlayerExtraData) {

    }

    @Override
    public void remotePlayerControllerAndXY(int remotePlayerId, int remotePlayerControllerState, float remotePlayerX, float remotePlayerY) {

    }

    @Override
    public void remotePlayerExit(int remotePlayerId, int remotePlayerExitCause) {

    }

    @Override
    public void extraFromServer(@NotNull String extraDataFromServer) {

    }

    @Override
    public void errorFromServer(int errorCode, @NotNull String errorMessage, @NotNull String errorDetails) {

    }

    @Override
    public void playerPingResponse() {

    }

    @Override
    public void remotePlayerPing(int remotePlayerId, int remotePlayerPing) {

    }

    @Override
    public void serverChat(int chatMessageId, @NotNull String chatMessageText, int chatMessageTextColor) {

    }

    @Override
    public void playerChat(int chatMessageId, int playerId, @NotNull String playerName, int playerColor, @NotNull String chatMessageText, int chatMessageTextColor) {

    }

    @Override
    public void serverInfoResponse(ServerInfo result) {

    }

    @Override
    public void serverTextToPlayer(@NotNull String text, int textColor) {

    }

    @Override
    public void fileData(@NotNull String headers, byte[] fileData) {

    }
}
