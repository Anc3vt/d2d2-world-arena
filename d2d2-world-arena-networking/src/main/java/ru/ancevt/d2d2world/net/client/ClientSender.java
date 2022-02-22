package ru.ancevt.d2d2world.net.client;

import lombok.extern.slf4j.Slf4j;
import ru.ancevt.net.tcpb254.connection.IConnection;

@Slf4j
public class ClientSender {
    private final IConnection connection;

    public ClientSender(IConnection connection) {
        this.connection = connection;
    }

    public void send(byte[] bytes) {
        try {
            connection.send(bytes);
        } catch(Exception e) { // failsafe purposes
            log.error(e.getMessage(), e);
        }
    }
}
