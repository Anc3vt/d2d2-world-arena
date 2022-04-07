/*
 *   D2D2 World Arena Desktop
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
package com.ancevt.d2d2world.net.client;

import com.ancevt.d2d2world.net.dto.client.PlayerPingReportDto;
import lombok.extern.slf4j.Slf4j;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.net.connection.IConnection;

import static com.ancevt.d2d2world.net.protocol.ProtocolImpl.createDtoMessage;
import static com.ancevt.d2d2world.net.serialization.JsonEngine.gson;

@Slf4j
public class ClientSender {
    private final IConnection connection;

    public ClientSender(IConnection connection) {
        this.connection = connection;
    }

    public void send(byte[] bytes) {
        try {
            connection.send(bytes);
        } catch (Exception e) { // failsafe purposes
            log.error(e.getMessage(), e);
        }
    }

    public void send(Dto dto) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);

        if (log.isDebugEnabled() && !(dto instanceof PlayerPingReportDto)) {
            log.debug("send <y>{}\n{}<>", className, json);
        }
        send(createDtoMessage(className, json));
    }
}
