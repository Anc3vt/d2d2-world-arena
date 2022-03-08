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
import ru.ancevt.d2d2world.D2D2World;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImplListener;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImplListenerAdapter;
import ru.ancevt.net.tcpb254.CloseStatus;
import ru.ancevt.net.tcpb254.connection.ConnectionListenerAdapter;
import ru.ancevt.net.tcpb254.connection.IConnection;
import ru.ancevt.net.tcpb254.server.ServerListener;
import ru.ancevt.util.args.Args;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl.MODULE_SERVER_PROTOCOL;
import static ru.ancevt.d2d2world.server.ServerConfig.*;
import static ru.ancevt.d2d2world.server.ServerStateInfo.MODULE_SERVER_STATE_INFO;
import static ru.ancevt.d2d2world.server.repl.ServerCommandProcessor.MODULE_COMMAND_PROCESSOR;
import static ru.ancevt.d2d2world.server.service.GeneralService.MODULE_GENERAL;
import static ru.ancevt.d2d2world.server.service.ServerUnit.MODULE_SERVER_UNIT;
import static ru.ancevt.d2d2world.server.simulation.ServerWorld.MODULE_WORLD;

@Slf4j
public class D2D2WorldArenaServerMain implements ServerListener, Thread.UncaughtExceptionHandler {

    public static void main(String @NotNull [] args) throws IOException {
        // Load serverConfig properties
        MODULE_SERVER_CONFIG.load();
        for (String arg : args) {
            if (arg.startsWith("-P")) {
                arg = arg.substring(2);
                String[] split = arg.split("=");
                String key = split[0];
                String value = split[1];
                MODULE_SERVER_CONFIG.setProperty(key, value);
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
        MODULE_SERVER_PROTOCOL.addServerProtocolImplListener(MODULE_GENERAL);

        MODULE_SERVER_STATE_INFO.setName(MODULE_SERVER_CONFIG.getString(SERVER_NAME));
        MODULE_SERVER_STATE_INFO.setVersion(getServerVersion());
        MODULE_SERVER_STATE_INFO.setMaxPlayers(MODULE_SERVER_CONFIG.getInt(SERVER_MAX_PLAYERS));
        MODULE_SERVER_STATE_INFO.setMap(MODULE_SERVER_CONFIG.getString(WORLD_DEFAULT_MAP));

        MODULE_SERVER_UNIT.server.addServerListener(this);

        Thread.setDefaultUncaughtExceptionHandler(this);

        MODULE_COMMAND_PROCESSOR.start();
    }

    public void start() {
        MODULE_SERVER_UNIT.server.asyncListenAndAwait(
                MODULE_SERVER_CONFIG.getString(SERVER_HOST),
                MODULE_SERVER_CONFIG.getInt(SERVER_PORT),
                2,
                SECONDS
        );
        ServerTimer.MODULE_TIMER.start();
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

    /**
     * {@link ServerListener method}
     */
    @Override
    public void serverStarted() {
        log.info("Version: " + getServerVersion());
        log.info("Server started at {}:{}", MODULE_SERVER_CONFIG.getString(SERVER_HOST), MODULE_SERVER_CONFIG.getInt(SERVER_PORT));

        MODULE_WORLD.start();
        D2D2World.init();
        MODULE_GENERAL.setMap(MODULE_SERVER_CONFIG.getString(WORLD_DEFAULT_MAP));
    }

    /**
     * {@link ServerListener method}
     */
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
                    MODULE_SERVER_PROTOCOL.removeServerProtocolImplListener(this);
                }
            }

            @Contract(pure = true)
            @Override
            public @NotNull String toString() {
                return "listener";
            }
        };

        MODULE_SERVER_PROTOCOL.addServerProtocolImplListener(serverProtocolImplListener);

        Async.runLater(MODULE_SERVER_CONFIG.getInt(SERVER_CONNECTION_TIMEOUT), TimeUnit.MILLISECONDS, () -> {
            if (!playerEntered.getValue()) {
                connection.closeIfOpen();
                MODULE_SERVER_PROTOCOL.removeServerProtocolImplListener(serverProtocolImplListener);
            }
        });

        connection.addConnectionListener(new ConnectionListenerAdapter() {
            @Override
            public void connectionClosed(CloseStatus status) {
                MODULE_SERVER_PROTOCOL.removeServerProtocolImplListener(serverProtocolImplListener);
            }
        });
    }

    /**
     * {@link ServerListener method}
     */
    @Override
    public void connectionClosed(@NotNull IConnection connection, @NotNull CloseStatus status) {
        log.info("Connection closed {}, status: {}", connection.toString(), status.toString());
        MODULE_GENERAL.connectionClosed(connection.getId(), status);
    }

    /**
     * {@link ServerListener method}
     */
    @Override
    public void connectionBytesReceived(@NotNull IConnection connection, byte[] bytes) {
        MODULE_SERVER_PROTOCOL.bytesReceived(connection.getId(), bytes);
    }

    /**
     * {@link ServerListener method}
     */
    @Override
    public void serverClosed(CloseStatus status) {
        log.info("Server closed " + status);
    }

    /**
     * {@link ServerListener method}
     */
    @Override
    public void connectionEstablished(IConnection connectionWithClient) {

    }

    /**
     * {@link Thread.UncaughtExceptionHandler method}
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Uncaught exception", e);
    }

}
