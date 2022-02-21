package ru.ancevt.d2d2world.net;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ancevt.d2d2world.net.protocol.ClientProtocolImpl;
import ru.ancevt.d2d2world.net.protocol.ClientProtocolListenerAdapter;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImpl;
import ru.ancevt.d2d2world.net.protocol.ServerProtocolImplListenerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class FileTransferTest {

    @BeforeEach
    void beforeEach() {
    }


    void createFile(String name, int length) throws IOException {
        byte[] bytes = new byte[length];
        new Random().nextBytes(bytes);
        Files.write(Path.of(name), bytes, WRITE, CREATE);
    }

    @Test
    void testFileTransferServerToClient() throws IOException {
        createFile("test.tmp", 128);

        byte[] bytes = Files.readAllBytes(Path.of("test.tmp"));

        ServerProtocolImpl spi = new ServerProtocolImpl();
        ClientProtocolImpl cpi = new ClientProtocolImpl();

        spi.addServerProtocolImplListener(new ServerProtocolImplListenerAdapter() {
            @Override
            public void fileData(int connectionId, @NotNull String headers, byte[] fileData) {

            }
        });

        cpi.addClientProtocolImplListener(new ClientProtocolListenerAdapter() {
            @Override
            public void fileData(@NotNull String headers, byte[] fileData) {
                super.fileData(headers, fileData);
            }
        });;

        spi.bytesReceived(0, bytes);
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of("test.tmp"));
    }
}
