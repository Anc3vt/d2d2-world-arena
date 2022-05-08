/*
 *   D2D2 Sockets
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.net.server;

import com.ancevt.commons.Holder;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.net.CloseStatus;
import com.ancevt.net.connection.ConnectionListener;
import com.ancevt.net.connection.IConnection;
import com.ancevt.net.connection.TcpB254Connection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.ancevt.commons.unix.UnixDisplay.debug;

public class TcpB254Server implements IServer {

    private static final int MAX_CONNECTIONS = Integer.MAX_VALUE;

    private final Set<IConnection> connections;
    private final Set<ServerListener> serverListeners;

    private ServerSocket serverSocket;

    private CountDownLatch countDownLatchForAsync;

    private boolean alive;

    private TcpB254Server() {
        connections = new CopyOnWriteArraySet<>();
        serverListeners = new HashSet<>();
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

            dispatchServerStarted();

            while (alive) {
                Socket socket = serverSocket.accept();
                IConnection connectionWithClient = TcpB254Connection.createServerSide(
                        getNextFreeConnectionId(),
                        socket
                );
                connectionWithClient.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void connectionEstablished() {
                        dispatchConnectionEstablished(connectionWithClient);
                    }

                    @Override
                    public void connectionBytesReceived(byte[] bytes) {
                        dispatchConnectionBytesReceived(connectionWithClient, bytes);
                    }

                    @Override
                    public void connectionClosed(CloseStatus status) {
                        connections.remove(connectionWithClient);
                        dispatchConnectionClosed(connectionWithClient, status);
                    }
                });
                connections.add(connectionWithClient);

                dispatchConnectionAccepted(connectionWithClient);

                new Thread(connectionWithClient::readLoop, "tcpB254_servconn" + connectionWithClient.getId()).start();
            }

            dispatchServerClosed(new CloseStatus());
        } catch (IOException e) {
            dispatchServerClosed(new CloseStatus(e));
        }

        alive = false;
    }

    @Override
    public void asyncListen(String host, int port) {
        Thread thread = new Thread(() -> listen(host, port), "tcpB254ServListen_" + host + "_" + port);
        thread.start();
    }

    @Override
    public boolean asyncListenAndAwait(String host, int port) {
        return asyncListenAndAwait(host, port, Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Override
    public synchronized boolean asyncListenAndAwait(String host, int port, long time, TimeUnit timeUnit) {
        asyncListen(host, port);
        countDownLatchForAsync = new CountDownLatch(1);
        try {
            return countDownLatchForAsync.await(time, timeUnit);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private void dispatchServerStarted() {
        serverListeners.forEach(ServerListener::serverStarted);
        if (countDownLatchForAsync != null) {
            countDownLatchForAsync.countDown();
            countDownLatchForAsync = null;
        }
    }

    private void dispatchServerClosed(CloseStatus status) {
        serverListeners.forEach(l -> l.serverClosed(status));
        if (countDownLatchForAsync != null) {
            countDownLatchForAsync.countDown();
            countDownLatchForAsync = null;
        }
    }

    private void dispatchConnectionEstablished(IConnection connectionWithClient) {
        serverListeners.forEach(l -> l.connectionEstablished(connectionWithClient));
    }

    private void dispatchConnectionClosed(IConnection connectionWithClient, CloseStatus status) {
        serverListeners.forEach(l -> l.connectionClosed(connectionWithClient, status));
    }

    private void dispatchConnectionBytesReceived(IConnection connection, byte[] data) {
        serverListeners.forEach(l -> l.connectionBytesReceived(connection, data));
    }

    private void dispatchConnectionAccepted(IConnection connectionWithClient) {
        serverListeners.forEach(l -> l.connectionAccepted(connectionWithClient));
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
            dispatchServerClosed(new CloseStatus((e)));
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
    public void sendToAll(byte[] bytes) {
        connections.forEach(c -> c.send(bytes));
    }

    @Override
    public Set<IConnection> getConnections() {
        return new HashSet<>(connections);
    }

    @Contract(" -> new")
    public static @NotNull IServer create() {
        return new TcpB254Server();
    }

    @Override
    public String toString() {
        return "TcpB254Server{" +
                "connections=" + connections.size() +
                ", serverListeners=" + serverListeners +
                ", serverSocket=" + serverSocket +
                ", alive=" + alive +
                '}';
    }

    public static void main(String[] args) {
        UnixDisplay.setEnabled(true);

        var lock = new Lock();

        Holder<Boolean> result = new Holder<>(false);

        IServer server = TcpB254Server.create();
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
                debug("com.ancevt.net.tcpb254.server.TcpServer.connectionBytesReceived(TcpServer:203): <A>" + new String(bytes, StandardCharsets.UTF_8));
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

        IConnection connection = TcpB254Connection.create();
        connection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connectionEstablished() {
                debug("<g>Connection connectionEstablished(TcpServer:225)");
                connection.send("Hello".repeat(10).getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public void connectionBytesReceived(byte[] bytes) {
                debug("<g>Connection (TcpServer:230) <A>" + new String(bytes, StandardCharsets.UTF_8));
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


        lock.lock(10, TimeUnit.SECONDS);
        server.close();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("done " + times);
        System.out.println("-".repeat(100));

        if(result.getValue()) {
            debug("TcpB254Server:317: <a><G>SUCCESS");
        }


        times--;
        if (times > 0) {
            main(null);
        }
    }

    private static int times = 0;
}