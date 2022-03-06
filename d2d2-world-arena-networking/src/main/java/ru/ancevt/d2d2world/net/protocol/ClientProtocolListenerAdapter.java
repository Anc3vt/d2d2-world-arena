/*
 *   D2D2 World Arena Desktop
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

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.net.client.ServerInfo;
import ru.ancevt.d2d2world.net.dto.ExtraDto;

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
    public void extraFromServer(@NotNull ExtraDto extraDto) {

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
