
package com.ancevt.d2d2world.server.service;

import com.ancevt.net.TcpFactory;
import com.ancevt.net.server.IServer;

public class ServerUnit {

    public static final ServerUnit MODULE_SERVER_UNIT = new ServerUnit();

    public final IServer server;

    private ServerUnit() {
        server = TcpFactory.createServer();
    }
}
