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
package ru.ancevt.net.messaging.connection;

import lombok.extern.slf4j.Slf4j;
import ru.ancevt.commons.io.ByteOutput;
import ru.ancevt.net.messaging.CloseStatus;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpB254Connection implements IConnection {

    private static final int MAX_CHUNK_SIZE = 254;

    private final int id;
    private final Set<ConnectionListener> listeners;
    private String host;
    private String remoteAddress;
    private int port;
    private int remotePort;

    private Socket socket;
    private volatile DataOutputStream dataOutputStream;

    private volatile CountDownLatch countDownLatchForAsync;

    private long bytesLoaded;
    private long bytesSent;

    TcpB254Connection(int id) {
        this.id = id;
        listeners = new CopyOnWriteArraySet<>();
    }

    TcpB254Connection(int id, Socket socket) {
        this(id);
        this.socket = socket;
        this.host = socket.getLocalAddress().getHostName();
        this.port = socket.getLocalPort();
        this.remoteAddress = socket.getRemoteSocketAddress().toString();
        this.remotePort = socket.getPort();
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void readLoop() {
        try {
            if (isOpen()) dispatchConnectionEstablished();

            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            ByteOutput byteOutput = ByteOutput.newInstance();

            while (isOpen()) {
                int len = in.readUnsignedByte();
                bytesLoaded++;

                if (len == 0) {
                    int left = MAX_CHUNK_SIZE;
                    while (left > 0) {
                        final int a = in.available();
                        if (a == 0) continue;
                        byte[] bytes = new byte[Math.min(a, left)];
                        left -= in.read(bytes);
                        byteOutput.write(bytes);
                    }
                    continue;
                }

                if (len == 255) {
                    byte[] array = byteOutput.toArray();
                    bytesLoaded += array.length;
                    dispatchConnectionBytesReceived(array);
                    byteOutput = ByteOutput.newInstance();
                    continue;
                }

                int left = len;
                while (left > 0) {
                    final int a = in.available();
                    if (a == 0) continue;
                    byte[] bytes = new byte[Math.min(a, left)];
                    left -= in.read(bytes);
                    byteOutput.write(bytes);
                    byte[] array = byteOutput.toArray();
                    bytesLoaded += array.length;
                    dispatchConnectionBytesReceived(array);
                    byteOutput = ByteOutput.newInstance();
                }


            }

        } catch (IOException e) {
            closeIfOpen();
        }

        closeIfOpen();
    }

    @Override
    public long bytesSent() {
        return bytesSent;
    }

    @Override
    public long bytesLoaded() {
        return bytesLoaded;
    }

    @Override
    public void connect(String host, int port) {
        this.socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));

            this.host = socket.getLocalAddress().getHostName();
            this.port = socket.getLocalPort();
            this.remoteAddress = socket.getRemoteSocketAddress().toString();
            this.remotePort = socket.getPort();

            dataOutputStream = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            dispatchConnectionClosed(new CloseStatus(e));
        }

        readLoop();
    }

    @Override
    public Thread asyncConnect(String host, int port) {
        Thread thread = new Thread(() -> connect(host, port), "tcpB254Conn_" + getId() + "to_" + host + "_" + port);
        thread.start();
        return thread;
    }

    @Override
    public boolean asyncConnectAndAwait(String host, int port, long time, TimeUnit timeUnit) {
        countDownLatchForAsync = new CountDownLatch(1);
        asyncConnect(host, port);
        try {
            return isOpen() || countDownLatchForAsync.await(time, timeUnit);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean asyncConnectAndAwait(String host, int port) {
        return asyncConnectAndAwait(host, port, Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public void close() {
        try {
            socket.close();
            dataOutputStream = null;
            dispatchConnectionClosed(new CloseStatus());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void closeIfOpen() {
        if (isOpen()) close();
    }

    @Override
    public boolean isOpen() {
        return dataOutputStream != null;
    }

    private synchronized void dispatchConnectionEstablished() {
        listeners.forEach(ConnectionListener::connectionEstablished);
        if (countDownLatchForAsync != null) {
            countDownLatchForAsync.countDown();
            countDownLatchForAsync = null;
        }
    }

    private synchronized void dispatchConnectionClosed(CloseStatus status) {
        dataOutputStream = null;
        listeners.forEach(l -> l.connectionClosed(status));
        if (countDownLatchForAsync != null) {
            countDownLatchForAsync.countDown();
            countDownLatchForAsync = null;
        }
    }

    private synchronized void dispatchConnectionBytesReceived(byte[] bytes) {
        listeners.forEach(l -> {
            try {
                l.connectionBytesReceived(bytes);
            } catch (Exception e) {
                // TODO: improve
                log.error(e.getMessage(), e);
            }
        });
    }


    @Override
    public void addConnectionListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public synchronized void send(byte[] bytes) {
        if (!isOpen()) return;

        if (bytes.length > MAX_CHUNK_SIZE) {
            sendComposite(bytes);
        } else {
            sendChunk(bytes, true);
        }
    }

    private void sendChunk(byte[] bytes, boolean fin) {
        try {
            if (socket.isConnected() && dataOutputStream != null) {
                if (bytes.length == 0) {
                    dataOutputStream.writeByte(255);
                    bytesSent++;
                } else {
                    dataOutputStream.writeByte(fin ? bytes.length : 0);
                    dataOutputStream.write(bytes);
                    bytesSent += 1 + bytes.length;

                }
            }

        } catch (SocketException e) {
            log.debug("Socket closed when send data");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void sendComposite(byte[] bytes) {
        int pos = 0;
        int length = 0;
        boolean fin = false;

        while (!fin) {
            length = Math.min(bytes.length - pos, MAX_CHUNK_SIZE);
            byte[] dest = new byte[length];
            System.arraycopy(bytes, pos, dest, 0, length);
            fin = length < MAX_CHUNK_SIZE;
            sendChunk(dest, fin);
            pos += length;
        }
    }

    private void validateSize(byte[] bytes) {
        if (bytes.length > MAX_CHUNK_SIZE)
            throw new IllegalStateException("Data size is " + bytes.length + " that more than " + MAX_CHUNK_SIZE);
    }

    @Override
    public String toString() {
        return "TcpB254Connection{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", remotePort=" + remotePort +
                ", socket=" + socket +
                ", isOpen=" + isOpen() +
                ", bytesLoaded=" + bytesLoaded +
                ", bytesSent=" + bytesSent +
                '}';
    }
}
