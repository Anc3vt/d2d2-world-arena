
package com.ancevt.net.server;

import com.ancevt.net.connection.IConnection;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface IServer {

    void listen(String host, int port);

    void asyncListen(String host, int port);

    boolean asyncListenAndAwait(String host, int port);

    boolean asyncListenAndAwait(String host, int port, long time, TimeUnit timeUnit);

    boolean isListening();

    void close();

    void addServerListener(ServerListener listener);

    void removeServerListener(ServerListener listener);

    Set<IConnection> getConnections();

    void sendToAll(byte[] bytes);
}
