package com.ancevt.d2d2world.transfer.test;

import com.ancevt.commons.concurrent.Lock;
import com.ancevt.d2d2world.data.file.FileDataUtils;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.net.transfer.FileReceiver;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.d2d2world.net.transfer.FileSender;
import com.ancevt.net.tcpb254.TcpFactory;
import com.ancevt.net.tcpb254.connection.ConnectionListenerAdapter;
import com.ancevt.net.tcpb254.connection.IConnection;
import com.ancevt.net.tcpb254.connection.TcpConnection;
import com.ancevt.net.tcpb254.server.IServer;
import com.ancevt.net.tcpb254.server.ServerListenerAdapter;
import com.ancevt.net.tcpb254.server.TcpServer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.ancevt.commons.unix.UnixDisplay.debug;
import static java.nio.file.StandardOpenOption.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FileTransferTest {

    @Contract("_, _ -> new")
    private @NotNull File createFile(String path, int filesize) {
        File dir = FileDataUtils.directory(path);

        String filePath = dir.getPath() + "/";
        String fileName = FileDataUtils.splitPath(path).getSecond();


        byte[] bytes = new byte[filesize];
        bytes[0] = (byte) 1;
        bytes[bytes.length - 1] = (byte) 2;

        try {
            Files.write(Path.of(filePath.concat(fileName)), bytes, WRITE, CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(filePath.concat(fileName));
    }

    @Contract(" -> new")
    private @NotNull IServer createServer() {
        return TcpServer.create();
    }

    @Test
    void testFileCreation() {
        File file = createFile("one/two/three/test", 100);
        assertThat(file.exists(), is(true));
        assertThat(file.length(), is(100L));
    }

    void testFileTransfer(int filesize) throws IOException {
        File file = createFile("one/two/three/test", filesize);

        IServer server = createServer();
        server.addServerListener(new ServerListenerAdapter() {
            @Override
            public void connectionEstablished(IConnection connectionWithClient) {
                FileSender fileSender = new FileSender(file.getPath(), true, true);
                fileSender.send(connectionWithClient);
            }
        });
        server.asyncListenAndAwait("0.0.0.0", 7777, 1, SECONDS);

        Lock lock = new Lock();

        IConnection connection = TcpFactory.createConnection(0);
        connection.addConnectionListener(new ConnectionListenerAdapter() {
            @Override
            public void connectionBytesReceived(byte[] bytes) {
                if ((bytes[0] & 0xFF) == MessageType.FILE_DATA) {
                    FileReceiverManager.INSTANCE.fileData(bytes);
                }
            }
        });
        connection.asyncConnectAndAwait("localhost", 7777, 1, SECONDS);

        FileReceiverManager.INSTANCE.addFileReceiverManagerListener(new FileReceiverManager.FileReceiverManagerListener() {
            @Override
            public void progress(FileReceiver fileReceiver) {
                System.out.println("Progress: " + fileReceiver);
            }

            @Override
            public void complete(FileReceiver fileReceiver) {
                System.out.println("Complete: " + fileReceiver);
                lock.unlockIfLocked();
            }
        });

        lock.lock(5, SECONDS);

        File receivedFile = new File("data/one/two/three/test");
        byte[] receivedBytes = Files.readAllBytes(Path.of(receivedFile.getPath()));

        assertThat((int) receivedFile.length(), is(filesize));
        assertThat(receivedBytes[0], is((byte) 1));
        assertThat(receivedBytes[receivedBytes.length - 1], is((byte) 2));

        server.close();
        connection.closeIfOpen();
    }

    @Test
    void testFileTransfer5() throws IOException {
        testFileTransfer(5);
    }

    @Test
    void testFileTransfer128() throws IOException {
        testFileTransfer(128);
    }

    @Test
    void testFileTransfer2048() throws IOException {
        testFileTransfer(2048);
    }

    @Test
    void testFileTransfer1M() throws IOException {
        testFileTransfer(1024 * 1024);
    }

    @Test
    void severalFiles() {
        final int filesize = 512 * 1024;
        List<File> files = new ArrayList<>();

        final int count = 100;

        for (int i = 0; i < count; i++) {
            files.add(createFile("one/two/three/test" + i, filesize));
        }

        IServer server = createServer();
        server.addServerListener(new ServerListenerAdapter() {
            @Override
            public void connectionEstablished(IConnection connectionWithClient) {
                files.forEach(file -> {
                    FileSender fileSender = new FileSender(file.getPath(), true, true);
                    fileSender.send(connectionWithClient);
                    debug("<y>send fileSender " + fileSender);
                });
            }
        });
        server.asyncListenAndAwait("0.0.0.0", 7777, 1, SECONDS);

        Lock lock = new Lock();

        IConnection connection = TcpConnection.create();
        connection.addConnectionListener(new ConnectionListenerAdapter() {
            @Override
            public void connectionBytesReceived(byte[] bytes) {
                if ((bytes[0] & 0xFF) == MessageType.FILE_DATA) {
                    FileReceiverManager.INSTANCE.fileData(bytes);
                }
            }
        });
        connection.asyncConnectAndAwait("localhost", 7777, 1, SECONDS);

        List<File> receivedFiles = new ArrayList<>();

        FileReceiverManager.INSTANCE.addFileReceiverManagerListener(new FileReceiverManager.FileReceiverManagerListener() {

            @Override
            public void progress(FileReceiver fileReceiver) {
                System.out.println("Progress: " + fileReceiver);
            }

            @Override
            public synchronized void complete(FileReceiver fileReceiver) {
                debug("<g>Complete: " + fileReceiver);
                receivedFiles.add(new File(fileReceiver.getPath()));

                if (receivedFiles.size() == files.size()) {
                    lock.unlockIfLocked();
                }
            }
        });

        lock.lock(5, SECONDS);

        receivedFiles.forEach(receivedFile -> {
            try {
                byte[] receivedBytes = Files.readAllBytes(Path.of(receivedFile.getPath()));
                assertThat((int) receivedFile.length(), is(filesize));
                assertThat(receivedBytes[0], is((byte) 1));
                assertThat(receivedBytes[receivedBytes.length - 1], is((byte) 2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        assertThat(receivedFiles.size(), is(count));

        server.close();
        connection.closeIfOpen();
    }

    @BeforeEach
    void beforeEachCleanup() {
        FileSender.parentDirectorySecurityEnabled = false;
        deleteDirectory(new File("data/"));
        deleteDirectory(new File("one/"));
    }

    @AfterEach
    void cleanup() {
        deleteDirectory(new File("data/"));
        deleteDirectory(new File("one/"));
    }

    void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}














