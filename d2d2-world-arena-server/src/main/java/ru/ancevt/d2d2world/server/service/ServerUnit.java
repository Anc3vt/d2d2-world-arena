package ru.ancevt.d2d2world.server.service;

import ru.ancevt.net.messaging.server.IServer;
import ru.ancevt.net.messaging.server.ServerFactory;

public class ServerUnit {

    public final IServer server;

    public ServerUnit() {
        server = ServerFactory.createTcpB254Server();
    }
}