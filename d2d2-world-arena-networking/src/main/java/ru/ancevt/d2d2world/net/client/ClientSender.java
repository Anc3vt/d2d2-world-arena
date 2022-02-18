package ru.ancevt.d2d2world.net.client;

import ru.ancevt.net.messaging.connection.IConnection;

public class ClientSender {
    private final IConnection connection;

    public ClientSender(IConnection connection) {
        this.connection = connection;
    }

    public void send(byte[] bytes) {
        connection.send(bytes);
    }
}
