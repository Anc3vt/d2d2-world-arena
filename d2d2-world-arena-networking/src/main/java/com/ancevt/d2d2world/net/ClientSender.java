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
package com.ancevt.d2d2world.net;

import com.ancevt.d2d2world.net.dto.client.PlayerPingReportDto;
import lombok.extern.slf4j.Slf4j;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.net.connection.IConnection;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2world.net.protocol.ProtocolImpl.createDtoMessage;
import static com.ancevt.d2d2world.net.serialization.JsonEngine.gson;

@Slf4j
public class ClientSender {
    private IConnection connection;

    public ClientSender() {
    }

    public void setConnection(IConnection connection) {
        this.connection = connection;
    }

    public IConnection getConnection() {
        return connection;
    }

    public void send(byte[] bytes) {
        try {
            connection.send(bytes);
        } catch (Exception e) { // failsafe purposes
            log.error(e.getMessage(), e);
        }
    }

    public void send(@NotNull Dto dto) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);

        if (log.isDebugEnabled() && !(dto instanceof PlayerPingReportDto)) {
            log.debug("send <y>{}\n{}<>", className, json);
        }
        send(createDtoMessage(className, json));
    }
}
