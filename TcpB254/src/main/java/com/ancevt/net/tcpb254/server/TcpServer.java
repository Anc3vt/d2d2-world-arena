package com.ancevt.net.tcpb254.server;

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.net.tcpb254.CloseStatus;
import com.ancevt.net.tcpb254.connection.ConnectionListener;
import com.ancevt.net.tcpb254.connection.IConnection;
import com.ancevt.net.tcpb254.connection.TcpConnection;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import static com.ancevt.commons.concurrent.Async.run;
import static com.ancevt.commons.unix.UnixDisplay.debug;

@Slf4j
public class TcpServer implements IServer {

    private static final int MAX_CONNECTIONS = Integer.MAX_VALUE;

    private final Set<IConnection> connections;
    private final Set<ServerListener> serverListeners;

    private ServerSocket serverSocket;

    private final Lock lock;

    private boolean alive;

    private TcpServer() {
        connections = new CopyOnWriteArraySet<>();
        serverListeners = new HashSet<>();
        lock = new Lock();
    }

    @Override
    public void listen(String host, int port) {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                throw new IllegalStateException("Server is listening " + this);
            }

            alive = true;

            serverSocket = new ServerSocket();

            serverSocket.bind(new InetSocketAddress(host, port));

            serverListeners.forEach(ServerListener::serverStarted);
            lock.unlockIfLocked();

            while (alive) {
                final Socket socket = serverSocket.accept();
                final IConnection connectionWithClient = TcpConnection.createServerSide(getNextFreeConnectionId(), socket);
                connectionWithClient.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void connectionEstablished() {
                        serverListeners.forEach(l -> l.connectionEstablished(connectionWithClient));
                    }

                    @Override
                    public void connectionBytesReceived(byte[] bytes) {
                        serverListeners.forEach(l -> l.connectionBytesReceived(connectionWithClient, bytes));
                    }

                    @Override
                    public void connectionClosed(CloseStatus status) {
                        connections.remove(connectionWithClient);
                        serverListeners.forEach(l -> l.connectionClosed(connectionWithClient, status));
                    }
                });
                connections.add(connectionWithClient);


                serverListeners.forEach(l -> l.connectionAccepted(connectionWithClient));

                new Thread(connectionWithClient::readLoop, "tcpConnectionThread" + connectionWithClient.getId()).start();
            }

            serverListeners.forEach(l -> l.serverClosed(new CloseStatus()));
            lock.unlockIfLocked();
        } catch (IOException e) {
            serverListeners.forEach(l -> l.serverClosed(new CloseStatus()));
            lock.unlockIfLocked();
        }

        alive = false;
    }

    private int getNextFreeConnectionId() {
        for (int i = 1; i < MAX_CONNECTIONS; i++) {
            if (getConnectionById(i) == null) return i;
        }
        throw new IllegalStateException("Connection count limit reached");
    }

    private IConnection getConnectionById(int id) {
        return connections.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void asyncListen(@NotNull String host, int port) {
        run(() -> listen(host, port));
    }

    @Override
    public boolean asyncListenAndAwait(String host, int port) {
        return asyncListenAndAwait(host, port, Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Override
    public boolean asyncListenAndAwait(String host, int port, long time, TimeUnit timeUnit) {
        asyncListen(host, port);
        return lock.lock(time, timeUnit);
    }

    @Override
    public boolean isListening() {
        return !serverSocket.isClosed() && alive;
    }

    @Override
    public void close() {
        try {
            if (!isListening()) {
                throw new IllegalStateException("Server not started");
            }

            connections.forEach(IConnection::closeIfOpen);
            serverSocket.close();

            alive = false;
        } catch (IOException e) {
            alive = false;
            serverListeners.forEach(l -> l.serverClosed(new CloseStatus(e)));
            lock.unlockIfLocked();
        }
    }

    @Override
    public void addServerListener(ServerListener listener) {
        serverListeners.add(listener);
    }

    @Override
    public void removeServerListener(ServerListener listener) {
        serverListeners.remove(listener);
    }

    @Override
    public Set<IConnection> getConnections() {
        return Set.copyOf(connections);
    }

    @Override
    public void sendToAll(byte[] bytes) {
        connections.forEach(c -> c.send(bytes));
    }

    public static IServer create() {
        return new TcpServer();
    }

    @Override
    public String toString() {
        return "TcpServer{" +
                "connections=" + connections +
                ", serverListeners=" + serverListeners +
                ", serverSocket=" + serverSocket +
                ", lock=" + lock +
                ", alive=" + alive +
                '}';
    }


    public static void main(String[] args) {
        UnixDisplay.setEnabled(true);

        var lock = new Lock();

        IServer server = TcpServer.create();
        server.addServerListener(new ServerListener() {
            @Override
            public void serverStarted() {
                debug("com.ancevt.net.tcpb254.server.TcpServer.serverStarted(TcpServer:186): <A>STARTED");
            }

            @Override
            public void connectionAccepted(IConnection connection) {
                debug("com.ancevt.net.tcpb254.server.TcpServer.connectionAccepted(TcpServer:193): <A>ACCEPTED");
                System.out.println(connection);
            }

            @Override
            public void connectionClosed(IConnection connection, CloseStatus status) {
                debug("<R>com.ancevt.net.tcpb254.server.TcpServer.connectionClosed(TcpServer:198): <A>CONN CLOSED");
            }

            @Override
            public void connectionBytesReceived(IConnection connection, byte[] bytes) {
                debug("com.ancevt.net.tcpb254.server.TcpServer.connectionBytesReceived(TcpServer:203): <a><Y>" + new String(bytes, StandardCharsets.UTF_8));
                connection.send("from server".getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public void serverClosed(CloseStatus status) {
                debug("com.ancevt.net.tcpb254.server.TcpServer.serverClosed(TcpServer:208): <A>SERVER CLOSED");
                server.removeServerListener(this);
            }

            @Override
            public void connectionEstablished(IConnection connectionWithClient) {
                debug("com.ancevt.net.tcpb254.server.TcpServer.connectionEstablished(TcpServer:213): <A>ESTABLISHED " + connectionWithClient);
            }
        });

        server.asyncListenAndAwait("0.0.0.0", 7777, 2, TimeUnit.SECONDS);

        Holder<Boolean> result = new Holder<>(false);

        IConnection connection = TcpConnection.create();
        connection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connectionEstablished() {
                debug("<g>Connection connectionEstablished(TcpServer:225)");
                connection.send("Hello".repeat(10).getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public void connectionBytesReceived(byte[] bytes) {
                debug("<g>Connection (TcpServer:230) <a><Y>" + new String(bytes, StandardCharsets.UTF_8));
                result.setValue(true);
                lock.unlockIfLocked();
                connection.removeConnectionListener(this);
            }

            @Override
            public void connectionClosed(CloseStatus status) {
                debug("<g>Connection connectionClosed");
            }
        });
        connection.asyncConnectAndAwait("localhost", 7777, 2, TimeUnit.SECONDS);


        lock.lock(2, TimeUnit.SECONDS);
        server.close();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("times: " + times);
        if (!result.getValue()) {
            debug("<R>FAIL!");
        } else {
            debug("<G>SUCCESS");
        }

        System.out.println("-".repeat(100));


        times--;
        if (times > 0) {
            main(null);
        }

    }

    private static int times = 1;
}




























