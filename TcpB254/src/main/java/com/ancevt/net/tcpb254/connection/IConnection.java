/*
 *   TCPB254
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.net.tcpb254.connection;

import java.util.concurrent.TimeUnit;

public interface IConnection {

    void connect(String host, int port);

    Thread asyncConnect(String host, int port);

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

    void closeIfOpen();

    void addConnectionListener(ConnectionListener listener);

    void removeConnectionListener(ConnectionListener listener);

    void send(byte[] bytes);

    long bytesSent();

    long bytesLoaded();

    static String getIpFromAddress(String address) {
        if (address.startsWith("/")) {
            address = address.replaceAll("/", "");
        }
        String[] split = address.split(":");
        return split[0];
    }
}
