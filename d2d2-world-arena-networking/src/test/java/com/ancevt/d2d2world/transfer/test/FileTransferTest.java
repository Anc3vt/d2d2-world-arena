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

package com.ancevt.d2d2world.transfer.test;

import com.ancevt.commons.concurrent.Lock;
import com.ancevt.commons.io.ByteInputReader;
import com.ancevt.d2d2world.data.file.FileSystemUtils;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.net.transfer.FileReceiver;
import com.ancevt.d2d2world.net.transfer.FileReceiverManager;
import com.ancevt.d2d2world.net.transfer.FileSender;
import com.ancevt.d2d2world.net.transfer.Headers;
import com.ancevt.net.TcpFactory;
import com.ancevt.net.connection.ConnectionListenerAdapter;
import com.ancevt.net.connection.IConnection;
import com.ancevt.net.connection.TcpConnection;
import com.ancevt.net.server.IServer;
import com.ancevt.net.server.ServerListenerAdapter;
import com.ancevt.net.server.TcpServer;
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

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FileTransferTest {

    @Contract("_, _ -> new")
    private @NotNull File createFile(String path, int filesize) {
        File dir = FileSystemUtils.directory(path);

        String filePath = dir.getPath() + "/";
        String fileName = FileSystemUtils.splitPath(path).getSecond();


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
                ByteInputReader in = ByteInputReader.newInstance(bytes);
                if (in.readByte() == MessageType.FILE_DATA) {
                    Headers headers = Headers.of(in.readUtf(short.class));
                    int contentLength = in.readInt();
                    byte[] fileData = in.readBytes(contentLength);
                    FileReceiverManager.INSTANCE.fileData(headers, fileData);
                }
            }
        });
        connection.asyncConnectAndAwait("localhost", 7777, 1, SECONDS);

        FileReceiverManager.INSTANCE.addFileReceiverManagerListener(new FileReceiverManager.FileReceiverManagerListener() {
            @Override
            public void fileReceiverProgress(FileReceiver fileReceiver) {
            }

            @Override
            public void fileReceiverComplete(FileReceiver fileReceiver) {
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
                });
            }
        });
        server.asyncListenAndAwait("0.0.0.0", 7777, 1, SECONDS);

        Lock lock = new Lock();

        IConnection connection = TcpConnection.create();
        connection.addConnectionListener(new ConnectionListenerAdapter() {
            @Override
            public void connectionBytesReceived(byte[] bytes) {
                ByteInputReader in = ByteInputReader.newInstance(bytes);
                if (in.readByte() == MessageType.FILE_DATA) {
                    Headers headers = Headers.of(in.readUtf(short.class));
                    int contentLength = in.readInt();
                    byte[] fileData = in.readBytes(contentLength);
                    FileReceiverManager.INSTANCE.fileData(headers, fileData);
                }
            }
        });
        connection.asyncConnectAndAwait("localhost", 7777, 1, SECONDS);

        List<File> receivedFiles = new ArrayList<>();

        FileReceiverManager.INSTANCE.addFileReceiverManagerListener(new FileReceiverManager.FileReceiverManagerListener() {

            @Override
            public void fileReceiverProgress(FileReceiver fileReceiver) {
            }

            @Override
            public synchronized void fileReceiverComplete(FileReceiver fileReceiver) {
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














