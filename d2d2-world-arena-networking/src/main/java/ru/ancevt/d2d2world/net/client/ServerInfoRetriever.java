package ru.ancevt.d2d2world.net.client;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.net.message.MessageType;
import ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl;
import ru.ancevt.net.tcpb254.CloseStatus;
import ru.ancevt.net.tcpb254.connection.ConnectionFactory;
import ru.ancevt.net.tcpb254.connection.ConnectionListenerAdapter;
import ru.ancevt.net.tcpb254.connection.IConnection;

@Slf4j
public class ServerInfoRetriever {

    public static void retrieve(String host,
                                int port,
                                @NotNull ResultFunction resultFunction,
                                @NotNull ErrorFunction errorFunction) {

        log.info("Retrieve server info {}:{}", host, port);
        IConnection connection = ConnectionFactory.createTcpB254Connection();

        connection.addConnectionListener(new ConnectionListenerAdapter() {

            private byte[] bytes;

            @Override
            public void connectionEstablished() {
                connection.send(ClientProtocolImpl.createMessageServerInfoRequest());
                log.info("Connection established");
            }

            @Override
            public void connectionClosed(CloseStatus status) {
                errorFunction.onError(status);
                connection.removeConnectionListener(this);
                log.info("Connection closed");
                resultFunction.onResult(ClientProtocolImpl.readServerInfoResponseBytes(bytes));
            }

            @Override
            public void connectionBytesReceived(byte[] bytes) {
                if (bytes[0] == MessageType.SERVER_INFO_RESPONSE) {
                    log.info("SERVER_INFO_RESPONSE bytes received, closing connection");
                    this.bytes = bytes;
                    connection.close();
                } else {
                    log.error("Server must not send other bytes than SERVER_INFO_RESPONSE");
                }
            }
        });
        connection.asyncConnect(host, port);
    }

    @FunctionalInterface
    public interface ResultFunction {
        void onResult(ServerInfo result);
    }

    @FunctionalInterface
    public interface ErrorFunction {
        void onError(CloseStatus closeStatus);
    }
}
