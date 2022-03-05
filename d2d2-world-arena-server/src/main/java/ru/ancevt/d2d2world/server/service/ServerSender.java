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
import ru.ancevt.net.tcpb254.server.IServer;


@Slf4j
public class ServerSender {

    public static final ServerSender MODULE_SENDER = new ServerSender();
    private final IServer serverUnit = ServerUnit.MODULE_SERVER_UNIT.server;

    private ServerSender() {
    }

    public void sendToPlayer(int playerId, byte[] bytes) {
        try {
            serverUnit.getConnections()
                    .stream()
                    .filter(c -> c.getId() == playerId)
                    .findAny()
                    .orElseThrow(IllegalStateException::new)
                    .send(bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendToAllExcluding(byte[] bytes, int excludingPlayerId) {
        try {
            serverUnit.getConnections()
                    .stream()
                    .filter(c -> c.getId() != excludingPlayerId)
                    .forEach(c -> c.send(bytes));
        } catch (Exception e) { // Exception here for failsafe purposes
            log.error(e.getMessage(), e);
        }
    }

    public void sendToAll(byte[] bytes) {
        try {
            serverUnit.sendToAll(bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
