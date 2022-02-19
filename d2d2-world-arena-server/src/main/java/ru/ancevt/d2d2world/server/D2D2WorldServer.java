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
package ru.ancevt.d2d2world.server;

import lombok.extern.slf4j.Slf4j;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import ru.ancevt.d2d2world.server.chat.ServerChat;
import ru.ancevt.d2d2world.server.player.ServerPlayerManager;
import ru.ancevt.d2d2world.server.repl.ServerCommandProcessor;
import ru.ancevt.d2d2world.server.service.GeneralService;
import ru.ancevt.d2d2world.server.service.ServerSender;
import ru.ancevt.d2d2world.server.service.ServerUnit;
import ru.ancevt.d2d2world.server.service.SyncService;
import ru.ancevt.net.messaging.CloseStatus;
import ru.ancevt.net.messaging.connection.IConnection;
import ru.ancevt.net.messaging.server.ServerListener;
import ru.ancevt.util.args.Args;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static ru.ancevt.d2d2world.server.Config.SERVER_HOST;
import static ru.ancevt.d2d2world.server.Config.SERVER_NAME;
import static ru.ancevt.d2d2world.server.Config.SERVER_PORT;
import static ru.ancevt.d2d2world.server.ModuleContainer.modules;

@Slf4j
public class D2D2WorldServer implements ServerListener, Thread.UncaughtExceptionHandler {

    public static void main(String[] args) throws IOException {
        // Load config properties
        Config config = new Config();
        config.load();
        for (String arg : args) {
            if (arg.startsWith("-P")) {
                arg = arg.substring(2);
                String[] split = arg.split("=");
                String key = split[0];
                String value = split[1];
                config.setProperty(key, value);
            }
        }

        Args a = new Args(args);

        // Modules initialization section: THE ORDER IS IMPORTANT!
        modules.add(config);
        modules.add(new ServerUnit());
        modules.add(new ServerSender());
        modules.add(new ServerChat());
        modules.add(new ServerPlayerManager());
        modules.add(new SyncService());
        modules.add(new ServerProtocolImpl());
        modules.add(new ServerCommandProcessor());
        modules.add(new ServerTimer());
        modules.add(new ServerStateInfo());
        modules.add(new GeneralService());

        String version = getServerVersion();

        if (a.contains("-v", "--version")) {
            System.out.println(version);
            return;
        }

        String host = a.get(new String[]{"--host", "-h"}, config.getString(SERVER_HOST, "0.0.0.0"));
        int port = a.get(Integer.class, new String[]{"--port", "-p"}, config.getInt(SERVER_PORT, 2245));
        String serverName = a.get(String.class, new String[]{"-n", "--name"},
                config.getString(SERVER_NAME, "D2D2 World Arena Server"));

        D2D2WorldServer server = new D2D2WorldServer(host, port, serverName, version);
        server.start();
    }

    private final String host;
    private final int port;

    private final String serverVersion;

    public D2D2WorldServer(String host, int port, String serverName, String serverVersion) {
        this.host = host;
        this.port = port;
        this.serverVersion = serverVersion;

        modules.get(ServerStateInfo.class).setName(serverName);
        modules.get(ServerStateInfo.class).setVersion(getServerVersion());

        modules.get(ServerUnit.class).server.addServerListener(this);

        Thread.setDefaultUncaughtExceptionHandler(this);

        modules.get(ServerCommandProcessor.class).start();
    }

    public void start() {
        modules.get(ServerUnit.class).server.asyncListenAndAwait(host, port, 2, TimeUnit.SECONDS);
        modules.get(ServerTimer.class).start();
    }

    public static String getServerVersion() {
        Properties properties = new Properties();
        try {
            properties.load(D2D2WorldServer.class.getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        return properties.getProperty("project.version");
    }

    @Override
    public void serverStarted() {
        log.info("Version: " + serverVersion);
        log.info("Server started at {}:{}", host, port);
    }

    @Override
    public void connectionAccepted(IConnection connection) {
        log.info("Connection accepted {}", connection.toString());
    }

    @Override
    public void connectionClosed(IConnection connection, CloseStatus status) {
        log.info("Connection closed {}, status: {}", connection.toString(), status.toString());
        modules.get(GeneralService.class).connectionClosed(connection.getId(), status);
    }

    @Override
    public void connectionBytesReceived(IConnection connection, byte[] bytes) {
        modules.get(ServerProtocolImpl.class).bytesReceived(connection.getId(), bytes);
    }

    @Override
    public void serverClosed(CloseStatus status) {
        log.info("Server closed " + status);
    }

    @Override
    public void connectionEstablished(IConnection connectionWithClient) {

    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Uncaught exception", e);
    }

}