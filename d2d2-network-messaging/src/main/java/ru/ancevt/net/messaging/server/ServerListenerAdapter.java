package ru.ancevt.net.messaging.server;

import ru.ancevt.net.messaging.CloseStatus;
import ru.ancevt.net.messaging.connection.IConnection;

public class ServerListenerAdapter implements ServerListener{

    @Override
    public void serverStarted() {

    }

    @Override
    public void connectionAccepted(IConnection connection) {

    }

    @Override
    public void connectionClosed(IConnection connection, CloseStatus status) {

    }

    @Override
    public void connectionBytesReceived(IConnection connection, byte[] bytes) {

    }

    @Override
    public void serverClosed(CloseStatus status) {

    }

    @Override
    public void connectionEstablished(IConnection connectionWithClient) {

    }
}
