package com.ancevt.net.tcpb254;

import com.ancevt.net.tcpb254.connection.IConnection;
import com.ancevt.net.tcpb254.connection.TcpB254Connection;
import com.ancevt.net.tcpb254.connection.TcpConnection;
import com.ancevt.net.tcpb254.server.IServer;
import com.ancevt.net.tcpb254.server.TcpB254Server;
import com.ancevt.net.tcpb254.server.TcpServer;

public class TcpFactory {

    public static boolean newImpl = true;

    public static IServer createServer() {
        if (newImpl) {
            return TcpServer.create();
        } else {
            return TcpB254Server.create();
        }
    }

    public static IConnection createConnection(int id) {
        if (newImpl) {
            return TcpConnection.create(id);
        } else {
            return TcpB254Connection.create(id);
        }
    }
}
