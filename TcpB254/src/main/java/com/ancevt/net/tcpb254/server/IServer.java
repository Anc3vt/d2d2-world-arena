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
package com.ancevt.net.tcpb254.server;

import com.ancevt.net.tcpb254.connection.IConnection;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface IServer {

    void listen(String host, int port);

    Thread asyncListen(String host, int port);

    boolean asyncListenAndAwait(String host, int port);

    boolean asyncListenAndAwait(String host, int port, long time, TimeUnit timeUnit);

    boolean isListening();

    void close();

    void addServerListener(ServerListener listener);

    void removeServerListener(ServerListener listener);

    Set<IConnection> getConnections();

    void sendToAll(byte[] bytes);
}
