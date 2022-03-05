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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.Holder;
import ru.ancevt.commons.concurrent.Async;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImplListener;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImplListenerAdapter;
import ru.ancevt.d2d2world.server.repl.ServerCommandProcessor;
import ru.ancevt.d2d2world.server.service.GeneralService;
import ru.ancevt.d2d2world.server.service.ServerUnit;
import ru.ancevt.net.tcpb254.CloseStatus;
import ru.ancevt.net.tcpb254.connection.ConnectionListenerAdapter;
import ru.ancevt.net.tcpb254.connection.IConnection;
import ru.ancevt.net.tcpb254.server.ServerListener;
import ru.ancevt.util.args.Args;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static ru.ancevt.d2d2world.server.ServerConfig.*;

@Slf4j
public class D2D2WorldArenaServerMain implements ServerListener, Thread.UncaughtExceptionHandler {

    public static void main(String @NotNull [] args) throws IOException {
        // Load serverConfig properties
        ServerConfig.INSTANCE.load();
        for (String arg : args) {
            if (arg.startsWith("-P")) {
                arg = arg.substring(2);
                String[] split = arg.split("=");
                String key = split[0];
                String value = split[1];
                ServerConfig.INSTANCE.setProperty(key, value);
            }
        }

        Args a = new Args(args);

        String version = getServerVersion();

        if (a.contains("-v", "--version")) {
            System.out.println(version);
            return;
        }

        D2D2WorldArenaServerMain server = new D2D2WorldArenaServerMain();
        server.start();
    }

    public D2D2WorldArenaServerMain() {
        ServerStateInfo.INSTANCE.setName(ServerConfig.INSTANCE.getString(SERVER_NAME));
        ServerStateInfo.INSTANCE.setVersion(getServerVersion());
        ServerStateInfo.INSTANCE.setMaxPlayers(ServerConfig.INSTANCE.getInt(SERVER_MAX_PLAYERS));

        ServerUnit.INSTANCE.server.addServerListener(this);

        Thread.setDefaultUncaughtExceptionHandler(this);

        ServerCommandProcessor.INSTANCE.start();
    }

    public void start() {
        ServerUnit.INSTANCE.server.asyncListenAndAwait(
                ServerConfig.INSTANCE.getString(SERVER_HOST),
                ServerConfig.INSTANCE.getInt(SERVER_PORT),
                2,
                TimeUnit.SECONDS
        );
        ServerTimer.INSTANCE.start();
    }

    public static String getServerVersion() {
        Properties properties = new Properties();
        try {
            properties.load(D2D2WorldArenaServerMain.class.getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        return properties.getProperty("project.version");
    }

    @Override
    public void serverStarted() {
        log.info("Version: " + getServerVersion());
        log.info("Server started at {}:{}", ServerConfig.INSTANCE.getString(SERVER_HOST), ServerConfig.INSTANCE.getInt(SERVER_PORT));
    }

    @Override
    public void connectionAccepted(@NotNull IConnection connection) {
        log.info("Connection accepted {}", connection.toString());

        // Provide timeout closing connection:
        Holder<Boolean> playerEntered = new Holder<>(false);

        ServerProtocolImplListener serverProtocolImplListener = new ServerProtocolImplListenerAdapter() {
            @Override
            public void playerEnterRequest(int playerId,
                                           @NotNull String playerName,
                                           @NotNull String clientProtocolVersion,
                                           @NotNull String extraData) {
                if (connection.getId() == playerId) {
                    playerEntered.setValue(true);
                    ServerProtocolImpl.INSTANCE.removeServerProtocolImplListener(this);
                }
            }

            @Contract(pure = true)
            @Override
            public @NotNull String toString() {
                return "listener";
            }
        };

        ServerProtocolImpl.INSTANCE.addServerProtocolImplListener(serverProtocolImplListener);

        Async.runLater(ServerConfig.INSTANCE.getInt(SERVER_CONNECTION_TIMEOUT), TimeUnit.MILLISECONDS, () -> {
            if (!playerEntered.getValue()) {
                connection.closeIfOpen();
                ServerProtocolImpl.INSTANCE.removeServerProtocolImplListener(serverProtocolImplListener);
            }
        });

        connection.addConnectionListener(new ConnectionListenerAdapter() {
            @Override
            public void connectionClosed(CloseStatus status) {
                ServerProtocolImpl.INSTANCE.removeServerProtocolImplListener(serverProtocolImplListener);
            }
        });
    }

    @Override
    public void connectionClosed(@NotNull IConnection connection, @NotNull CloseStatus status) {
        log.info("Connection closed {}, status: {}", connection.toString(), status.toString());
        GeneralService.INSTANCE.connectionClosed(connection.getId(), status);
    }

    @Override
    public void connectionBytesReceived(@NotNull IConnection connection, byte[] bytes) {
        ServerProtocolImpl.INSTANCE.bytesReceived(connection.getId(), bytes);
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
