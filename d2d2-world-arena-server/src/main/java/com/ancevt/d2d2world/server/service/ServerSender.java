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
package com.ancevt.d2d2world.server.service;

import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.protocol.ClientProtocolImpl;
import com.ancevt.d2d2world.net.protocol.ClientProtocolImplListener;
import com.ancevt.d2d2world.sync.ISyncDataReceiver;
import com.ancevt.d2d2.tcp.server.IServer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2world.net.protocol.ProtocolImpl.createDtoMessage;
import static com.ancevt.d2d2world.net.serialization.JsonEngine.gson;


@Slf4j
public class ServerSender {

    public static final ServerSender MODULE_SENDER = new ServerSender();
    private final IServer serverUnit = ServerUnit.MODULE_SERVER_UNIT.server;

    private ServerSender() {
    }

    public void sendToPlayer(int playerId, Dto dto) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);
        log.debug("sendToPlayer {} <y>{}\n{}<>", playerId, className, json);
        sendToPlayer(playerId, createDtoMessage(className, json));
    }

    public void sendToAllExcluding(Dto dto, int excludingPlayerId) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);
        log.debug("sendToAllExcluding !{} <y>{}\n{}<>", excludingPlayerId, className, json);
        sendToAllExcluding(createDtoMessage(className, json), excludingPlayerId);
    }

    public void sendToAll(Dto dto) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);
        log.debug("sendToAll <y>{}\n{}<>", className, json);
        sendToAll(createDtoMessage(className, json));
    }

    public void sendToAllExcluding(byte[] bytes, int excludingPlayerId) {
        debug(bytes);

        try {
            serverUnit.getConnections()
                    .stream()
                    .filter(c -> c.getId() != excludingPlayerId)
                    .forEach(c -> c.send(bytes));
        } catch (Exception e) { // Exception here for failsafe purposes
            log.error(e.getMessage(), e);
        }
    }

    public synchronized void sendToAll(byte[] bytes) {
        debug(bytes);

        try {
            serverUnit.sendToAll(bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendToPlayer(int playerId, byte[] bytes) {
        debug(bytes);

        try {
            serverUnit.getConnections()
                    .stream()
                    .filter(c -> c.getId() == playerId)
                    .findAny()
                    .orElseThrow()
                    .send(bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean listenersAdded = false;

    private void debug(byte[] bytes) {
        if(!listenersAdded) {
            ClientProtocolImpl.MODULE_CLIENT_PROTOCOL.addClientProtocolImplListener(new ClientProtocolImplListener() {
                @Override
                public void dtoFromServer(@NotNull Dto extraDto) {
                }

                @Override
                public void playerPingResponse() {

                }

                @Override
                public void fileData(@NotNull String headers, byte[] fileData) {

                }

                @Override
                public void serverSyncData(byte @NotNull [] syncData) {
                    new ISyncDataReceiver() {
                        @Override
                        public void setEnabled(boolean enabled) {

                        }

                        @Override
                        public boolean isEnabled() {
                            return false;
                        }

                        @Override
                        public void bytesReceived(byte[] bytes) {
                        }
                    }.bytesReceived(syncData);
                }
            });
            listenersAdded = true;
        }
    }
}



































