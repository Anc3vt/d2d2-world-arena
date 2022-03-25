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
package com.ancevt.d2d2world.net.protocol;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import com.ancevt.commons.io.ByteInputReader;
import com.ancevt.commons.io.ByteOutputWriter;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.message.Message;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.d2d2world.net.transfer.Headers;

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
        Message message = Message.of(bytes);
        ByteInputReader in = message.inputReader();

        try {
            switch (message.getType()) {

                case MessageType.PING -> {
                    log.trace("received <b>PING<>");
                    clientProtocolImplListeners.forEach(ClientProtocolImplListener::playerPingResponse);
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
            log.error("message" + message.getBytes().length, e);
        }
    }

    public static byte[] createMessagePlayerWeaponSwitch(int delta) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_WEAPON_SWITCH)
                .writeByte(delta + 1)
                .toByteArray();
    }

    public static byte[] createMessagePlayerAimXY(float aimX, float aimY) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.CLIENT_PLAYER_AIM_XY)
                .writeFloat(aimX)
                .writeFloat(aimY)
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



