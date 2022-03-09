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
package ru.ancevt.d2d2world.net.client;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.net.tcpb254.CloseStatus;

public interface ClientListener {

    void remotePlayerIntroduce(@NotNull RemotePlayer remotePlayer);

    void remotePlayerExit(@NotNull RemotePlayer remotePlayer);

    void playerEnterServer(int localPlayerId, int localPlayerColor, @NotNull String serverProtocolVersion);

    void serverChat(int chatMessageId, @NotNull String chatMessageText, int chatMessageTextColor);

    void playerChat(int chatMessageId,
                    int playerId,
                    @NotNull String playerName,
                    int playerColor,
                    @NotNull String chatMessageText,
                    int textColor);

    void clientConnectionClosed(@NotNull CloseStatus status);

    void clientConnectionEstablished();

    void remotePlayerEnterServer(int remotePlayerId, @NotNull String remotePlayerName, int remotePlayerColor);

    void serverInfo(@NotNull ServerInfo result);

    void serverTextToPlayer(@NotNull String text, int textColor);

    void fileData(@NotNull String headers, byte[] fileData);

    void rconResponse(String rconResponseData);

    void mapContentLoaded(String mapFilename);

    void localPlayerActorGameObjectId(int playerActorGameObjectId);
}
