/*
 *   D2D2 World Arena Server
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
package ru.ancevt.d2d2world.server.service;

import ru.ancevt.net.tcpb254.server.IServer;
import ru.ancevt.net.tcpb254.server.ServerFactory;

public class ServerUnit {

    public static final ServerUnit MODULE_SERVER_UNIT = new ServerUnit();

    public final IServer server;

    private ServerUnit() {
        server = ServerFactory.createTcpB254Server();
    }
}
