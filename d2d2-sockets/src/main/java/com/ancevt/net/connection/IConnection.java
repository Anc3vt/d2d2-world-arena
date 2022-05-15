
package com.ancevt.net.connection;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public interface IConnection {

    void connect(String host, int port);

    void asyncConnect(String host, int port);

    boolean asyncConnectAndAwait(String host, int port);

    boolean asyncConnectAndAwait(String host, int port, long time, TimeUnit timeUnit);

    int getId();

    String getHost();

    int getPort();

    String getRemoteAddress();

    int getRemotePort();

    void readLoop();

    boolean isOpen();

    void close();

    default void closeIfOpen() {
        if(isOpen()) close();
    }

    void addConnectionListener(ConnectionListener listener);

    void removeConnectionListener(ConnectionListener listener);

    void send(byte[] bytes);

    long bytesSent();

    long bytesLoaded();

    static String getIpFromAddress(@NotNull String address) {
        if (address.contains("/")) {
            address = address.replaceAll("^.*/", "");
        }

        if(address.contains(":")) {
            String[] split = address.split(":");
            return split[0];
        } else {
            return address;
        }

    }
}
