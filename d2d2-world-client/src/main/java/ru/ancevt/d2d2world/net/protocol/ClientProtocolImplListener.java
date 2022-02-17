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

public non-sealed interface ClientProtocolImplListener extends ProtocolImplListener {

    void rconResponse(String rconResponseData);

    void playerEnterResponse(int playerId, int color, String serverProtocolVersion);

    void remotePlayerEnter(int remotePlayerId, String remotePlayerName, int remotePlayerColor);

    void remotePlayerIntroduce(int remotePlayerId,
                               String remotePlayerName,
                               int remotePlayerColor,
                               String remotePlayerExtraData);

    void remotePlayerControllerAndXY(int remotePlayerId,
                                     int remotePlayerControllerState,
                                     float remotePlayerX,
                                     float remotePlayerY);

    void remotePlayerExit(int remotePlayerId, int remotePlayerExitCause);

    void extraFromServer(String extraDataFromServer);

    void errorFromServer(int errorCode, String errorMessage, String errorDetails);

    void playerPingResponse();

    void remotePlayerPing(int remotePlayerId, int remotePlayerPing);
    
    void serverChat(int chatMessageId, String chatMessageText);

    void playerChat(int chatMessageId, int playerId, String playerName, int playerColor, String chatMessageText);
}
