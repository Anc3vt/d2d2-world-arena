/*
 *   D2D2 World Arena Desktop
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
package com.ancevt.d2d2world.net.client;

import com.ancevt.commons.io.ByteInputReader;
import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.d2d2world.net.dto.client.ServerInfoRequestDto;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2.tcp.CloseStatus;
import com.ancevt.d2d2.tcp.TcpFactory;
import com.ancevt.d2d2.tcp.connection.ConnectionListenerAdapter;
import com.ancevt.d2d2.tcp.connection.IConnection;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2world.net.protocol.ProtocolImpl.createDtoMessage;
import static com.ancevt.d2d2world.net.serialization.JsonEngine.gson;

@Slf4j
public class ServerInfoRetriever {

    private ServerInfoRetriever() {
    }

    public static void retrieve(String host,
                                int port,
                                @NotNull ResultFunction resultFunction,
                                @NotNull ErrorFunction errorFunction) {

        log.info("Retrieve server info {}:{}", host, port);
        IConnection connection = TcpFactory.createConnection(0);

        connection.addConnectionListener(new ConnectionListenerAdapter() {

            private byte[] bytes;

            @Override
            public void connectionEstablished() {
                String className = ServerInfoRequestDto.INSTANCE.getClass().getName();
                String json = gson().toJson(ServerInfoRequestDto.INSTANCE);
                connection.send(createDtoMessage(className, json));
                log.info("Connection established");
            }

            @Override
            public void connectionClosed(CloseStatus status) {
                errorFunction.onError(status);
                connection.removeConnectionListener(this);
                log.info("Connection closed");
                if (bytes != null) {
                    ByteInputReader in = ByteInputReader.newInstance(bytes);
                    in.readByte();
                    String className = in.readUtf(short.class);
                    String extraDataFromServer = in.readUtf(int.class);
                    log.info("received DTO " + className + "\n" + extraDataFromServer);
                    try {
                        if (gson().fromJson(extraDataFromServer, Class.forName(className)) instanceof ServerInfoDto dto) {
                            resultFunction.onResult(dto);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

            @Override
            public void connectionBytesReceived(byte[] bytes) {
                if ((bytes[0] & 0xFF) == MessageType.DTO) {
                    log.info("EXTRA bytes received, closing connection");
                    this.bytes = bytes;
                    connection.close();
                } // else ignoring
            }
        });
        connection.asyncConnect(host, port);
    }

    @FunctionalInterface
    public interface ResultFunction {
        void onResult(ServerInfoDto result);
    }

    @FunctionalInterface
    public interface ErrorFunction {
        void onError(CloseStatus closeStatus);
    }

    public static void main(String[] args) {
        UnixDisplay.setEnabled(true);
        ServerInfoRetriever.retrieve(
                "localhost",
                2255,
                System.out::println,
                System.out::println
        );
    }
}
