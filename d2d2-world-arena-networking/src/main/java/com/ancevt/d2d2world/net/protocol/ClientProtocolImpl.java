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
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.d2d2world.net.transfer.Headers;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ancevt.d2d2world.net.serialization.JsonEngine.gson;

@Slf4j
public final class ClientProtocolImpl extends ProtocolImpl {

    public static final ClientProtocolImpl MODULE_CLIENT_PROTOCOL = new ClientProtocolImpl();

    private final List<ClientProtocolImplListener> clientProtocolImplListeners;

    private ClientProtocolImpl() {
        this.clientProtocolImplListeners = new CopyOnWriteArrayList<>();
    }

    public void addClientProtocolImplListener(@NotNull ClientProtocolImplListener l) {
        clientProtocolImplListeners.add(l);
    }

    public void removeClientProtocolImplListener(@NotNull ClientProtocolImplListener l) {
        clientProtocolImplListeners.remove(l);
    }

    public void bytesReceived(byte[] bytes) {
        ByteInputReader in = ByteInputReader.newInstance(bytes);

        int type = in.readByte();

        try {
            switch (type) {

                case MessageType.PING -> {
                    log.trace("received <b>PING<>");
                    clientProtocolImplListeners.forEach(ClientProtocolImplListener::playerPingResponse);
                }

                case MessageType.SERVER_PLAYER_ATTACK -> {
                    int playerId = in.readInt();
                    log.trace("received <b>SERVER_PLAYER_SHOOT<> {}", playerId);
                    clientProtocolImplListeners.forEach(l -> l.playerShoot(playerId));
                }

                case MessageType.SERVER_SYNC_DATA -> {
                    int length = in.readShort();
                    byte[] syncData = in.readBytes(length);
                    clientProtocolImplListeners.forEach(l -> l.serverSyncData(syncData));
                }

                case MessageType.FILE_DATA -> {
                    Headers headers = Headers.of(in.readUtf(short.class));
                    int contentLength = in.readInt();
                    if (log.isTraceEnabled()) {
                        log.trace("received <b>FILE_DATA<>\n<g>{}<><contentLength={}><>", headers, contentLength);
                    }
                    byte[] fileData = in.readBytes(contentLength);
                    FileReceiverManager.INSTANCE.fileData(headers, fileData);
                }

                case MessageType.DTO -> {
                    String className = in.readUtf(short.class);
                    String json = in.readUtf(int.class);
                    if (log.isDebugEnabled()) {
                        log.debug("received <b>DTO<><g> {}\n{}<>", className, json);
                    }
                    Dto extraDto = (Dto) gson().fromJson(json, Class.forName(className));
                    clientProtocolImplListeners.forEach(l -> l.dtoFromServer(extraDto));
                }

            }

        } catch (Exception e) {
            log.error("message" + bytes.length, e);
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public static byte @NotNull [] createMessageHook(int hookGameObjectId) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_HOOK)
                .writeInt(hookGameObjectId)
                .toByteArray();
    }

    public static byte[] createMessagePlayerWeaponSwitch(int delta) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_WEAPON_SWITCH)
                .writeByte(delta + 1)
                .toByteArray();
    }

    public static byte[] createMessageHealthReport(int healthValue, int damagingGameObjectId) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_HEALTH_REPORT)
                .writeShort(healthValue)
                .writeInt(damagingGameObjectId)
                .toByteArray();
    }

    public static byte[] createMessagePlayerAimXY(float aimX, float aimY) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_AIM_XY)
                .writeFloat(aimX)
                .writeFloat(aimY)
                .toByteArray();

    }

    public static byte[] createMessagePlayerXY(float x, float y) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_XY)
                .writeFloat(x)
                .writeFloat(y)
                .toByteArray();
    }

    public static byte[] createMessagePlayerController(int controllerState) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_CONTROLLER)
                .writeByte(controllerState)
                .toByteArray();
    }

    public static byte[] createMessageFileRequest(@NotNull String headers) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_REQUEST_FILE)
                .writeUtf(short.class, headers)
                .toByteArray();
    }
}



