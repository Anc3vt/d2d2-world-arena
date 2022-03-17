package com.ancevt.net.tcpb254.connection;

import com.ancevt.commons.concurrent.Lock;
import com.ancevt.commons.io.ByteOutput;
import com.ancevt.net.tcpb254.CloseStatus;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpConnection implements IConnection {

    private final Object sendMonitor = new Object();
    private final Object receiveMonitor = new Object();

    private final int id;
    private final Set<ConnectionListener> listeners;
    private String host;
    private String remoteAddress;
    private int port;
    private int remotePort;

    private Socket socket;
    private volatile DataOutputStream dataOutputStream;

    private long bytesLoaded;
    private long bytesSent;

    private final Lock connectLock;

    private boolean serverSide;

    TcpConnection(int id) {
        this.id = id;
        listeners = new CopyOnWriteArraySet<>();

        connectLock = new Lock();

        log.debug("New connection {}", this);
        serverSide = false;
    }

    TcpConnection(int id, @NotNull Socket socket) {
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
        serverSide = true;
    }

    private boolean isServerSide() {
        return serverSide;
    }

    @Override
    public void readLoop() {
        try {
            if (isOpen()) {
                listeners.forEach(ConnectionListener::connectionEstablished);
                connectLock.unlockIfLocked();
            }

            var in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            var byteOutput = ByteOutput.newInstance();

            while (isOpen()) {
                while(true) {
                    final int a = in.available();
                    if (a >= 4) break;
                }
                int len = in.readInt();
                bytesLoaded += 4;
                int left = len;
                while (left > 0) {
                    final int a = in.available();
                    if (a == 0) continue;
                    byte[] bytes = new byte[a];
                    int read = in.read(bytes);
                    left -= read;
                    byteOutput.write(bytes);
                }

                if (byteOutput.hasData()) {
                    byte[] data = byteOutput.toArray();
                    byteOutput = ByteOutput.newInstance();
                    dispatchConnectionBytesReceived(data);
                }
            }
        } catch (IOException e) {
            closeIfOpen();
        }
        closeIfOpen();
    }

    @Override
    public void connect(String host, int port) {
        this.socket = new Socket();
        log.debug("Connecting to {}:{}, {}", host, port, this);
        try {
            socket.connect(new InetSocketAddress(host, port));
            this.host = socket.getLocalAddress().getHostName();
            this.port = socket.getLocalPort();
            this.remoteAddress = socket.getRemoteSocketAddress().toString();
            this.remotePort = socket.getPort();
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            listeners.forEach(l -> l.connectionClosed(new CloseStatus(e)));
            connectLock.unlockIfLocked();
        }
        readLoop();
    }

    @Override
    public void asyncConnect(String host, int port) {
        Thread thread = new Thread(() -> connect(host, port), "tcpConn_" + getId() + "to_" + host + "_" + port);
        thread.start();
    }

    @Override
    public boolean asyncConnectAndAwait(String host, int port, long time, TimeUnit timeUnit) {
        asyncConnect(host, port);
        return isOpen() || connectLock.lock(time, timeUnit);
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
    public boolean isOpen() {
        return dataOutputStream != null;
    }

    @Override
    public void close() {
        try {
            socket.close();
            dataOutputStream = null;
            log.debug("Close connection {}", this);
            listeners.forEach(l -> l.connectionClosed(new CloseStatus()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void dispatchConnectionBytesReceived(byte[] bytes) {
        synchronized (receiveMonitor) {
            listeners.forEach(l -> {
                try {
                    l.connectionBytesReceived(bytes);
                } catch (Exception e) {
                    // TODO: improve
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public void addConnectionListener(@NotNull ConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConnectionListener(@NotNull ConnectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void send(byte[] bytes) {
        if (!isOpen()) return;

        synchronized (sendMonitor) {
            try {
                dataOutputStream.writeInt(bytes.length);
                dataOutputStream.write(bytes);
                bytesSent += 4 + bytes.length;
            } catch (SocketException e) {
                log.debug("Socket closed when send data");
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
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
    public String toString() {
        return "TcpConnection{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", port=" + port +
                ", remotePort=" + remotePort +
                ", socket=" + socket +
                ", bytesLoaded=" + bytesLoaded +
                ", bytesSent=" + bytesSent +
                '}';
    }

    @Contract(" -> new")
    public static @NotNull IConnection create() {
        return create(0);
    }

    @Contract("_ -> new")
    public static @NotNull IConnection create(int id) {
        return new TcpConnection(id);
    }

    @Contract("_, _ -> new")
    public static @NotNull IConnection createServerSide(int id, Socket socket) {
        return new TcpConnection(id, socket);
    }
}
