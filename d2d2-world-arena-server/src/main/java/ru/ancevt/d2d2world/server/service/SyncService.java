/*
 *   D2D2 World Arena Server
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
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import ru.ancevt.d2d2world.server.player.ServerPlayerManager;

@Slf4j
public class SyncService {
    public static final SyncService MODULE_SYNC = new SyncService();


    private SyncService() {
    }

    public void syncFirstLevel() {
        /*ServerPlayerManager.MODULE_PLAYER_MANAGER.getPlayerList().forEach(p ->
                ServerSender.MODULE_SENDER.sendToAllExcluding(
                        ServerProtocolImpl.createMessageRemotePlayerControllerAndXY(
                                p.getId(),
                                p.getControllerState(),
                                p.getX(),
                                p.getY()
                        ),
                        p.getId())
        );*/
    }

    public void syncSecondLevel() {

    }

    public void syncThirdLevel() {
        sendPings();
    }

    private void sendPings() {
        ServerPlayerManager.MODULE_PLAYER_MANAGER.getPlayerList().forEach(p -> {
            ServerSender.MODULE_SENDER.sendToAllExcluding(
                    ServerProtocolImpl.createMessageRemotePlayerPingValue(p.getId(), p.getPingValue()),
                    p.getId()
            );
        });
    }
}
