package ru.ancevt.d2d2world.net.protocol;

import org.jetbrains.annotations.NotNull;

public class ServerProtocolImplListenerAdapter implements ServerProtocolImplListener {
    @Override
    public void playerEnterRequest(int playerId, @NotNull String playerName, @NotNull String clientProtocolVersion, @NotNull String extraData) {

    }

    @Override
    public void playerExitRequest(int playerId) {

    }

    @Override
    public void playerControllerAndXYReport(int playerId, int controllerState, float x, float y) {

    }

    @Override
    public void playerTextToChat(int playerId, @NotNull String text) {

    }

    @Override
    public void playerPingReport(int playerId, int ping) {

    }

    @Override
    public void extraFromPlayer(int playerId, @NotNull String extraData) {

    }

    @Override
    public void rconLogin(int playerId, @NotNull String passwordHash) {

    }

    @Override
    public void rconCommand(int playerId, @NotNull String commandText, @NotNull String extraData) {

    }

    @Override
    public void errorFromPlayer(int errorCode, @NotNull String errorMessage, @NotNull String errorDetails) {

    }

    @Override
    public void playerPingRequest(int playerId) {

    }

    @Override
    public void serverInfoRequest(int connectionId) {

    }

    @Override
    public void requestFile(int connectionId, @NotNull String headers) {

    }

    @Override
    public void fileData(int connectionId, @NotNull String headers, byte[] fileData) {

    }
}
