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
import ru.ancevt.d2d2world.net.dto.ExtraDto;

public class ServerProtocolImplListenerAdapter implements ServerProtocolImplListener {
    @Override
    public void playerEnterRequest(int playerId, @NotNull String playerName, @NotNull String clientProtocolVersion, @NotNull String extraData) {

    }

    @Override
    public void playerExitRequest(int playerId) {

    }

    @Override
    public void playerController(int playerId, int controllerState) {

    }

    @Override
    public void playerTextToChat(int playerId, @NotNull String text) {

    }

    @Override
    public void playerPingReport(int playerId, int ping) {

    }

    @Override
    public void extraFromPlayer(int playerId, ExtraDto extraDto) {

    }

    @Override
    public void ping(int playerId) {

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
    public void serverInfoRequest(int playerId) {

    }

    @Override
    public void requestFile(int playerId, @NotNull String headers) {

    }

}
