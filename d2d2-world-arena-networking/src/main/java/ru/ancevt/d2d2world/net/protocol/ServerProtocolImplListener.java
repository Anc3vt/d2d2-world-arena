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

public non-sealed interface ServerProtocolImplListener extends ProtocolImplListener {

    void playerEnterRequest(int playerId, String playerName, String clientProtocolVersion, String extraData);

    void playerExitRequest(int playerId);

    void playerControllerAndXYReport(int playerId, int controllerState, float x, float y);

    void playerTextToChat(int playerId, String text);

    void playerPingReport(int playerId, int ping);

    void extraFromPlayer(int playerId, String extraData);

    void rconLogin(int playerId, String login, String passwordHash);

    void rconCommand(int playerId, String commandText, String extraData);

    void errorFromPlayer(int errorCode, String errorMessage, String errorDetails);

    void playerPingRequest(int playerId);

    void serverInfoRequest(int connectionId);
}
