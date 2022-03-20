package com.ancevt.d2d2.tcp;

import com.ancevt.d2d2.tcp.connection.IConnection;
import com.ancevt.d2d2.tcp.connection.TcpB254Connection;
import com.ancevt.d2d2.tcp.connection.TcpConnection;
import com.ancevt.d2d2.tcp.server.IServer;
import com.ancevt.d2d2.tcp.server.TcpB254Server;
import com.ancevt.d2d2.tcp.server.TcpServer;
import org.jetbrains.annotations.NotNull;

public class TcpFactory {

    public static boolean newImpl = true;

    public static @NotNull IServer createServer() {
        if (newImpl) {
            return TcpServer.create();
        } else {
            return TcpB254Server.create();
        }
    }

    public static @NotNull IConnection createConnection(int id) {
        if (newImpl) {
            return TcpConnection.create(id);
        } else {
            return TcpB254Connection.create(id);
        }
    }
}
