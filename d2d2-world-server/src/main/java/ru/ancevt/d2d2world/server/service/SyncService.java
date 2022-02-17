/*
 *   D2D2 World Server
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
package ru.ancevt.d2d2world.server.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import ru.ancevt.d2d2world.server.player.PlayerManager;

@Slf4j
public class SyncService {

    private final PlayerManager playerManager;
    private final ServerSender serverSender;

    public SyncService(@NotNull PlayerManager playerManager,
                       @NotNull ServerSender serverSender) {

        this.playerManager = playerManager;
        this.serverSender = serverSender;
    }

    public void syncFirstLevel() {
        playerManager.getPlayerList().forEach(p ->
                serverSender.sendToAllExcluding(
                        ServerProtocolImpl.createMessageRemotePlayerControllerAndXY(
                                p.getId(),
                                p.getControllerState(),
                                p.getX(),
                                p.getY()
                        ),
                        p.getId())
        );
    }

    public void syncSecondLevel() {

    }

    public void syncThirdLevel() {
        log.debug("syncThirdLevel");
        sendPings();
    }

    private void sendPings() {
        playerManager.getPlayerList().forEach(p -> {
            serverSender.sendToAllExcluding(
                    ServerProtocolImpl.createMessageRemotePlayerPingValue(p.getId(), p.getPingValue()),
                    p.getId()
            );
        });
    }
}