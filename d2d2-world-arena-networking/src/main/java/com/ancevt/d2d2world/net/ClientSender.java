
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

    public void send(@NotNull Dto dto) {
        String className = dto.getClass().getName();
        String json = gson().toJson(dto);

        if (log.isDebugEnabled() && !(dto instanceof PlayerPingReportDto)) {
            log.debug("send <y>{}\n{}<>", className, json);
        }
        send(createDtoMessage(className, json));
    }
}
