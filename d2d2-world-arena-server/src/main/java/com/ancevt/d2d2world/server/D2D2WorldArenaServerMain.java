/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancevt.d2d2world.server;

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.d2d2.media.SoundSystem;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.net.dto.Dto;
import com.ancevt.d2d2world.net.dto.client.ServerInfoRequestDto;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.net.protocol.ServerProtocolImplListener;
import com.ancevt.net.CloseStatus;
import com.ancevt.net.connection.ConnectionListener;
import com.ancevt.net.connection.ConnectionListenerAdapter;
import com.ancevt.net.connection.IConnection;
import com.ancevt.net.server.ServerListener;
import com.ancevt.util.args.Args;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Properties;

import static com.ancevt.d2d2world.net.protocol.ServerProtocolImpl.MODULE_SERVER_PROTOCOL;
import static com.ancevt.d2d2world.server.ServerState.MODULE_SERVER_STATE;
import static com.ancevt.d2d2world.server.config.ServerConfig.CONFIG;
import static com.ancevt.d2d2world.server.config.ServerConfig.SERVER_HOST;
import static com.ancevt.d2d2world.server.config.ServerConfig.SERVER_MAX_PLAYERS;
import static com.ancevt.d2d2world.server.config.ServerConfig.SERVER_NAME;
import static com.ancevt.d2d2world.server.config.ServerConfig.SERVER_PORT;
import static com.ancevt.d2d2world.server.config.ServerConfig.WORLD_DEFAULT_MAP;
import static com.ancevt.d2d2world.server.repl.ServerCommandProcessor.MODULE_COMMAND_PROCESSOR;
import static com.ancevt.d2d2world.server.scene.ServerWorldScene.SERVER_WORLD_SCENE;
import static com.ancevt.d2d2world.server.service.GeneralService.MODULE_GENERAL;
import static com.ancevt.d2d2world.server.service.ServerUnit.MODULE_SERVER_UNIT;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class D2D2WorldArenaServerMain implements ServerListener, Thread.UncaughtExceptionHandler {

    public static void main(String @NotNull [] args) throws IOException {
        SoundSystem.setEnabled(false);

        // Load serverConfig properties
        CONFIG.load();
        for (String arg : args) {
            if (arg.startsWith("-P")) {
                arg = arg.substring(2);
                String[] split = arg.split("=");
                String key = split[0];
                String value = split[1];
                CONFIG.setProperty(key, value);
            }
            if (arg.equals("--colorize-logs")) {
                UnixDisplay.setEnabled(true);
            }
        }

        Args a = Args.of(args);
        String version = getServerVersion();

        if (a.contains("-v", "--version")) {
            System.out.println(version);
            System.exit(0);
        }

        D2D2WorldArenaServerMain server = new D2D2WorldArenaServerMain();
        server.start();
    }

    public D2D2WorldArenaServerMain() {
        MODULE_SERVER_PROTOCOL.addServerProtocolImplListener(MODULE_GENERAL);

        MODULE_SERVER_STATE.setName(CONFIG.getProperty(SERVER_NAME));
        MODULE_SERVER_STATE.setVersion(getServerVersion());
        MODULE_SERVER_STATE.setMaxPlayers(CONFIG.getInt(SERVER_MAX_PLAYERS, 100));
        MODULE_SERVER_STATE.setMap(CONFIG.getProperty(WORLD_DEFAULT_MAP, "map0.wam"));

        MODULE_SERVER_UNIT.server.addServerListener(this);

        Thread.setDefaultUncaughtExceptionHandler(this);

        MODULE_COMMAND_PROCESSOR.start();
    }

    public void start() {
        MODULE_SERVER_UNIT.server.asyncListenAndAwait(
                CONFIG.getProperty(SERVER_HOST),
                CONFIG.getInt(SERVER_PORT, 3333),
                2,
                SECONDS
        );
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
        log.info("<y>Version: <g>{}<>", getServerVersion());
        log.info("Server started at {}:{}", CONFIG.getProperty(SERVER_HOST), CONFIG.getInt(SERVER_PORT, 3333));

        SERVER_WORLD_SCENE.start();
        D2D2World.init(true, false);
        MODULE_GENERAL.setMap(CONFIG.getProperty(WORLD_DEFAULT_MAP, "map0.wam"));
    }

    /**
     * {@link ServerListener method}
     */
    @Override
    public void connectionAccepted(@NotNull IConnection connection) {
        log.info("<g>Connection accepted {}<>", connection.toString());

        // Provide timeout closing connection:
        Holder<Boolean> playerEntered = new Holder<>(false);

        ServerProtocolImplListener serverProtocolImplListener = new ServerProtocolImplListener() {

            @Override
            public void dtoFromPlayer(int playerId, Dto dto) {
                if (dto instanceof ServerInfoRequestDto) {
                    if (connection.getId() == playerId) {
                        playerEntered.setValue(true);
                        MODULE_SERVER_PROTOCOL.removeServerProtocolImplListener(this);
                    }
                }
            }

            @Contract(pure = true)
            @Override
            public @NotNull String toString() {
                return "listener";
            }
        };

        MODULE_SERVER_PROTOCOL.addServerProtocolImplListener(serverProtocolImplListener);

        // HANDSHAKE CHECK
        connection.addConnectionListener(new ConnectionListener() {

            private boolean handshakeReceived;

            @Override
            public void connectionEstablished() {
                Async.runLater(10, SECONDS, () -> {
                    if (!handshakeReceived) connection.closeIfOpen();
                });
            }

            @Override
            public void connectionBytesReceived(byte[] bytes) {
                if ((bytes[0] & 0xFF) == MessageType.HANDSHAKE) {
                    handshakeReceived = true;
                    connection.removeConnectionListener(this);
                } else {
                    connection.closeIfOpen();
                }
            }

            @Override
            public void connectionClosed(CloseStatus status) {
                log.info("No handshake packet received!");
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
