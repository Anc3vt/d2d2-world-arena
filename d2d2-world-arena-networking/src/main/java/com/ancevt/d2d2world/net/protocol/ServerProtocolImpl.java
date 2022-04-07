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
package com.ancevt.d2d2world.net.protocol;

import com.ancevt.d2d2world.net.dto.client.PlayerPingReportDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import com.ancevt.commons.io.ByteInputReader;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.message.Message;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.d2d2world.net.transfer.Headers;

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
        Message message = Message.of(bytes);
        ByteInputReader in = message.inputReader();

        try {

            switch (message.getType()) {

                case MessageType.PING -> {
                    log.trace("received <b>PING<> {}", connectionId);
                    serverProtocolImplListeners.forEach(l -> l.ping(connectionId));
                }

                case MessageType.CLIENT_DAMAGE_REPORT -> {
                    int damageValue = in.readShort();
                    int damagingGameObjectId = in.readInt();
                    log.trace("received <b>CLIENT_DAMAGE_REPORT<> {} value:{}  damagingGameObjectId: {}",
                            connectionId, damageValue, damagingGameObjectId);

                    serverProtocolImplListeners.forEach(l -> l.playerDamageReport(connectionId, damageValue, damagingGameObjectId));
                }

                case MessageType.CLIENT_PLAYER_WEAPON_SWITCH -> {
                    int delta = in.readByte() - 1;
                    log.trace("received <b>CLIENT_DAMAGE_REPORT<> {} value: {}", connectionId, delta + 1);
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
                    log.trace("received <b>CLIENT_PLAYER_CONTROLLER<> from {}", connectionId);
                    serverProtocolImplListeners.forEach(l -> l.playerController(connectionId, controlState));
                }

                case MessageType.CLIENT_REQUEST_FILE -> {
                    String headers = in.readUtf(short.class);
                    if (log.isTraceEnabled()) {
                        log.trace("received <b>CLIENT_REQUEST_FILE<> from {}\n<g>{}<>", connectionId, headers);
                    }
                    serverProtocolImplListeners.forEach(l -> l.requestFile(connectionId, headers));
                }

                case MessageType.FILE_DATA -> {
                    Headers headers = Headers.of(in.readUtf(short.class));
                    int contentLength = in.readInt();
                    byte[] fileData = in.readBytes(contentLength);
                    if (log.isTraceEnabled()) {
                        log.trace("received <b>FILE_DATA<> from {}\n<g>{}\n<contentLength={}<>",
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
                        log.debug("received <b>DTO<> from {} <g>{}\n{}<>", connectionId, className, json);
                    }
                    serverProtocolImplListeners.forEach(l -> l.dtoFromPlayer(connectionId, dto));
                }


            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
