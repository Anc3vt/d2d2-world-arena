
package com.ancevt.net;

import com.ancevt.net.connection.IConnection;
import com.ancevt.net.connection.TcpConnection;
import com.ancevt.net.server.IServer;
import com.ancevt.net.server.TcpServer;
import org.jetbrains.annotations.NotNull;

public class TcpFactory {

    public static @NotNull IServer createServer() {
        return TcpServer.create();
    }

    public static @NotNull IConnection createConnection(int id) {
        return TcpConnection.create(id);
    }
}
