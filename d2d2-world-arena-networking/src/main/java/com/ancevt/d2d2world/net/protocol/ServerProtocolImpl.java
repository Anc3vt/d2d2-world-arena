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
package com.ancevt.d2d2world.net.protocol;

import com.ancevt.commons.io.ByteInputReader;
import com.ancevt.commons.io.ByteOutputWriter;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.dto.client.PlayerPingReportDto;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.d2d2world.net.transfer.Headers;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ancevt.d2d2world.net.serialization.JsonEngine.gson;

@Slf4j
public final class ServerProtocolImpl extends ProtocolImpl {

    public static final ServerProtocolImpl MODULE_SERVER_PROTOCOL = new ServerProtocolImpl();

    private final List<ServerProtocolImplListener> serverProtocolImplListeners;

    private ServerProtocolImpl() {
        this.serverProtocolImplListeners = new CopyOnWriteArrayList<>();
    }

    public void addServerProtocolImplListener(@NotNull ServerProtocolImplListener l) {
        serverProtocolImplListeners.add(l);
    }

    public void removeServerProtocolImplListener(@NotNull ServerProtocolImplListener l) {
        serverProtocolImplListeners.remove(l);
    }

    public void bytesReceived(int connectionId, byte[] bytes) {
        ByteInputReader in = ByteInputReader.newInstance(bytes);

        int type = in.readByte();

        try {

            switch (type) {

                case MessageType.HANDSHAKE -> {
                    log.trace("received <b>HANDSHAKE<> {}", connectionId);
                }

                case MessageType.PING -> {
                    log.trace("received <b>PING<> {}", connectionId);
                    serverProtocolImplListeners.forEach(l -> l.ping(connectionId));
                }

                case MessageType.CLIENT_PLAYER_HOOK -> {
                    int hookGameObjectId = in.readInt();
                    log.trace("received <b>CLIENT_PLAYER_HOOK<> {} {}", connectionId, hookGameObjectId);

                    serverProtocolImplListeners.forEach(l -> l.playerHook(connectionId, hookGameObjectId));
                }

                case MessageType.CLIENT_HEALTH_REPORT -> {
                    int healthValue = in.readShort();
                    int damagingGameObjectId = in.readInt();
                    log.trace("received <b>CLIENT_HEALTH_REPORT<> {} healthValue:{}  damagingGameObjectId: {}",
                            connectionId, healthValue, damagingGameObjectId);

                    serverProtocolImplListeners.forEach(l -> l.playerHealthReport(connectionId, healthValue, damagingGameObjectId));
                }

                case MessageType.CLIENT_PLAYER_WEAPON_SWITCH -> {
                    int delta = in.readByte() - 1;
                    log.trace("received <b>CLIENT_PLAYER_WEAPON_SWITCH<> {} value: {}", connectionId, delta + 1);
                    serverProtocolImplListeners.forEach(l -> l.playerWeaponSwitch(connectionId, delta));
                }

                case MessageType.CLIENT_PLAYER_XY -> {
                    float x = in.readFloat();
                    float y = in.readFloat();
                    serverProtocolImplListeners.forEach(l -> l.playerXY(connectionId, x, y));
                }

                case MessageType.CLIENT_PLAYER_AIM_XY -> {
                    float x = in.readFloat();
                    float y = in.readFloat();
                    //log.trace("received <b>CLIENT_PLAYER_AIM_XY<> {} {} {}", connectionId, x, y);
                    serverProtocolImplListeners.forEach(l -> l.playerAimXY(connectionId, x, y));
                }

                case MessageType.CLIENT_PLAYER_CONTROLLER -> {
                    int controlState = in.readByte();
                    log.trace("received <b>CLIENT_PLAYER_CONTROLLER<> {}", connectionId);
                    serverProtocolImplListeners.forEach(l -> l.playerController(connectionId, controlState));
                }

                case MessageType.CLIENT_REQUEST_FILE -> {
                    String headers = in.readUtf(short.class);
                    if (log.isTraceEnabled()) {
                        log.trace("received <b>CLIENT_REQUEST_FILE<> {}\n<g>{}<>", connectionId, headers);
                    }
                    serverProtocolImplListeners.forEach(l -> l.requestFile(connectionId, headers));
                }

                case MessageType.FILE_DATA -> {
                    Headers headers = Headers.of(in.readUtf(short.class));
                    int contentLength = in.readInt();
                    byte[] fileData = in.readBytes(contentLength);
                    if (log.isTraceEnabled()) {
                        log.trace("received <b>FILE_DATA<> {}\n<g>{}\n<contentLength={}<>",
                                connectionId,
                                headers,
                                contentLength);
                    }
                    FileReceiverManager.INSTANCE.fileData(headers, fileData);
                }

                case MessageType.DTO -> {
                    String className = in.readUtf(short.class);
                    String json = in.readUtf(int.class);
                    Dto dto = (Dto) gson().fromJson(json, Class.forName(className));
                    if (log.isDebugEnabled() && !(dto instanceof PlayerPingReportDto) ) {
                        log.debug("received <b>DTO<> {} <g>{}\n{}<>", connectionId, className, json);
                    }
                    serverProtocolImplListeners.forEach(l -> l.dtoFromPlayer(connectionId, dto));
                }


            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static byte @NotNull [] createMessagePlayerAttack(int playerId) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_PLAYER_ATTACK)
                .writeInt(playerId)
                .toByteArray();
    }
}
