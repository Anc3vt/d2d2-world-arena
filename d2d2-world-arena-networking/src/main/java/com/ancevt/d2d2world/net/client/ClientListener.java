/*
 *   D2D2 World Arena Networking
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2world.net.client;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.net.CloseStatus;

import java.time.LocalDateTime;

public interface ClientListener {

    void playerExit(@NotNull Player remotePlayer);

    void playerEnterServer(int localPlayerId, int localPlayerColor, @NotNull String serverProtocolVersion, @NotNull LocalDateTime serverStartTime);

    void serverChat(int chatMessageId, @NotNull String chatMessageText, int chatMessageTextColor);

    void playerChat(int chatMessageId,
                    int playerId,
                    @NotNull String playerName,
                    int playerColor,
                    @NotNull String chatMessageText,
                    int textColor);

    void clientConnectionClosed(@NotNull CloseStatus status);

    void clientConnectionEstablished();

    void playerEnterServer(int remotePlayerId, @NotNull String remotePlayerName, int remotePlayerColor);

    void serverInfo(@NotNull ServerInfoDto result);

    void serverTextToPlayer(@NotNull String text, int textColor);

    void fileData(@NotNull String headers, byte[] fileData);

    void rconResponse(String rconResponseData);

    void mapContentLoaded(String mapFilename);

    void localPlayerActorGameObjectId(int playerActorGameObjectId);

    void playerDeath(int deadPlayerId, int killerPlayerId);

    void playerChatEvent(int playerId, String action);
}
