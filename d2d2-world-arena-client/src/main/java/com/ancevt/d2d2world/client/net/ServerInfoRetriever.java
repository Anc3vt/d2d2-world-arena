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
package com.ancevt.d2d2world.client.net;

import com.ancevt.commons.io.ByteInputReader;
import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.d2d2world.net.dto.client.ServerInfoRequestDto;
import com.ancevt.d2d2world.net.dto.server.ServerInfoDto;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.net.CloseStatus;
import com.ancevt.net.TcpFactory;
import com.ancevt.net.connection.ConnectionListenerAdapter;
import com.ancevt.net.connection.IConnection;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2world.net.protocol.ProtocolImpl.createDtoMessage;
import static com.ancevt.d2d2world.net.serialization.JsonEngine.gson;

@Slf4j
public class ServerInfoRetriever {

    private ServerInfoRetriever() {
    }

    public static void retrieve(String host, int port, @NotNull ResultFunction resultFunction, @NotNull ErrorFunction errorFunction) {

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
