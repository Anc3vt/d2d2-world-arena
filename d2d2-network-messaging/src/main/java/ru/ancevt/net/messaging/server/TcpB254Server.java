/*
 *   Networking Library, network-messaging
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
package ru.ancevt.net.messaging.server;

import ru.ancevt.net.messaging.CloseStatus;
import ru.ancevt.net.messaging.connection.ConnectionFactory;
import ru.ancevt.net.messaging.connection.ConnectionListener;
import ru.ancevt.net.messaging.connection.IConnection;
import ru.ancevt.net.messaging.connection.TcpB254Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TcpB254Server implements IServer {

    private static final int MAX_CONNECTIONS = Integer.MAX_VALUE;

    private final Set<IConnection> connections;
    private final Set<ServerListener> serverListeners;

    private ServerSocket serverSocket;

    private CountDownLatch countDownLatchForAsync;

    private boolean alive;

    public TcpB254Server() {
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
                IConnection connectionWithClient = ConnectionFactory.createServerSideTcpB254Connection(
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

                new Thread(
                        () -> ((TcpB254Connection) connectionWithClient).read(),
                        "tcpConnectionThread" + connectionWithClient.getId()
                ).start();
            }

            dispatchServerClosed(new CloseStatus());
        } catch (IOException e) {
            dispatchServerClosed(new CloseStatus(e));
        }

        alive = false;
    }

    @Override
    public Thread asyncListen(String host, int port) {
        Thread thread = new Thread(()->listen(host,port), "tcpServListen_" + host + "_" + port);
        thread.start();
        return thread;
    }

    @Override
    public synchronized boolean asyncListenAndAwait(String host, int port) {
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
        if(countDownLatchForAsync != null) {
            countDownLatchForAsync.countDown();
            countDownLatchForAsync = null;
        }
    }

    private void dispatchServerClosed(CloseStatus status) {
        serverListeners.forEach(l -> l.serverClosed(status));
        if(countDownLatchForAsync != null) {
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
            if(!isListening()) {
                throw new IllegalStateException("Server not started");
            }

            connections.forEach(c -> {
                if (c.isOpen()) c.close();
            });
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

    @Override
    public String toString() {
        return "TcpB254Server{" +
                "connections=" + connections.size() +
                ", serverListeners=" + serverListeners +
                ", serverSocket=" + serverSocket +
                ", alive=" + alive +
                '}';
    }
}
