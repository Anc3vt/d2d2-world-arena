
package com.ancevt.net;

import com.ancevt.net.connection.IConnection;
import com.ancevt.net.connection.TcpB254Connection;
import com.ancevt.net.connection.TcpConnection;
import com.ancevt.net.server.IServer;
import com.ancevt.net.server.TcpB254Server;
import com.ancevt.net.server.TcpServer;
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
