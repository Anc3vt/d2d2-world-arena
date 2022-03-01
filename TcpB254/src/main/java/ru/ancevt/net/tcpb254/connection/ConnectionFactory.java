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
package ru.ancevt.net.tcpb254.connection;

import java.net.Socket;

public class ConnectionFactory {

    private static final int DEFAULT_CONNECTION_ID = 0;

    public static IConnection createTcpB254Connection(int id) {
        return new TcpB254Connection(id);
    }

    public static IConnection createTcpB254Connection() {
        return createTcpB254Connection(DEFAULT_CONNECTION_ID);
    }

    public static IConnection createServerSideTcpB254Connection(int id, Socket socket) {
        return new TcpB254Connection(id, socket);
    }
}
