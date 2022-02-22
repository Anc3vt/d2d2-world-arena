package ru.ancevt.d2d2world.net.client;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.net.message.MessageType;
import ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl;
import ru.ancevt.net.tcpb254.CloseStatus;
import ru.ancevt.net.tcpb254.connection.ConnectionFactory;
import ru.ancevt.net.tcpb254.connection.ConnectionListenerAdapter;
import ru.ancevt.net.tcpb254.connection.IConnection;

public class ServerInfoRetriever {

    public static void retrieve(String host,
                                int port,
                                @NotNull ResultFunction resultFunction,
                                @NotNull ErrorFunction errorFunction) {

        IConnection connection = ConnectionFactory.createTcpB254Connection();
        connection.addConnectionListener(new ConnectionListenerAdapter() {
            @Override
            public void connectionEstablished() {
                connection.send(ClientProtocolImpl.createMessageServerInfoRequest());
            }

            @Override
            public void connectionClosed(CloseStatus status) {
                errorFunction.onError(status);
                connection.close();
            }

            @Override
            public void connectionBytesReceived(byte[] bytes) {
                if(bytes[0] == MessageType.SERVER_INFO_RESPONSE) {
                    resultFunction.onResult(ClientProtocolImpl.readServerInfoResponseBytes(bytes));
                    connection.removeConnectionListener(this);
                    connection.close();
                }
            }
        });
        connection.asyncConnect(host, port);
    }

    @FunctionalInterface
    public interface ResultFunction {
        void onResult(ServerInfoRetrieveResult result);
    }

    @FunctionalInterface
    public interface ErrorFunction {
        void onError(CloseStatus closeStatus);
    }
}
