/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancevt.d2d2world.server.service;

import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.net.server.IServer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2world.net.protocol.ProtocolImpl.createDtoMessage;
import static com.ancevt.d2d2world.net.serialization.JsonEngine.gson;
import static com.ancevt.d2d2world.server.player.ServerPlayerManager.PLAYER_MANAGER;


@Slf4j
public class ServerSender {

    public static final ServerSender SENDER = new ServerSender();
    private final IServer serverUnit = ServerUnit.MODULE_SERVER_UNIT.server;

    private ServerSender() {
    }

    public void sendToPlayer(int playerId, @NotNull Dto dto) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);
        log.debug("sendToPlayer {} <y>{}\n{}<>", playerId, className, json);
        sendToPlayer(playerId, createDtoMessage(className, json));
    }

    public void sendToAllExcluding(@NotNull Dto dto, int excludingPlayerId) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);
        log.debug("sendToAllExcluding !{} <y>{}\n{}<>", excludingPlayerId, className, json);
        sendToAllExcluding(createDtoMessage(className, json), excludingPlayerId);
    }

    public void sendToAll(@NotNull Dto dto) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);
        log.debug("sendToAll <y>{}\n{}<>", className, json);
        sendToAll(createDtoMessage(className, json));
    }

    public void sendToAllOfRoom(@NotNull Dto dto, String roomId) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);
        log.debug("sendToAllOfRoom <y>{}\n{}\n{}<>", roomId, className, json);
        sendToAllOfRoom(createDtoMessage(className, json), roomId);
    }

    public void sendToAllOfRoomExcluding(@NotNull Dto dto, String roomId, int excludingPlayerId) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);
        log.debug("sendToAllOfRoomExcluding <y>{}\n{}\n{}<>", roomId, className, json);
        sendToAllOfRoomExcluding(createDtoMessage(className, json), roomId, excludingPlayerId);
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

    public synchronized void sendToAll(byte[] bytes) {
        try {
            serverUnit.sendToAll(bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendToPlayer(int playerId, byte[] bytes) {
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

    public void sendToAllOfRoom(byte[] bytes, String roomId) {
        PLAYER_MANAGER.getPlayerListInRoom(roomId).forEach(player -> player.getConnection().send(bytes));
    }

    public void sendToAllOfRoomExcluding(byte[] bytes, String roomId, int excludingPlayerId) {
        PLAYER_MANAGER.getPlayerListInRoom(roomId)
                .stream().filter(player -> player.getId() != excludingPlayerId)
                .forEach(player -> player.getConnection().send(bytes));
    }
}



































