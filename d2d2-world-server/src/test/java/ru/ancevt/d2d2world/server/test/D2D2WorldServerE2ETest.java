package ru.ancevt.d2d2world.server.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.ancevt.d2d2world.server.D2D2WorldServer;

public class D2D2WorldServerE2ETest {

    private static final int CLIENT_CONNECTION_ID = 0;
    private static final String CLIENT_PROTOCOL_VERSION = "1.0";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 7777;

    private D2D2WorldServer server;

    @BeforeEach
    void beforeEach() {
        server = new D2D2WorldServer("0.0.0.0", PORT);
        server.start();
    }

    @AfterEach
    void afterEach() {
        server.exit();
    }

    @Test
    @Disabled
    void testConnect() {
    }

    @Test
    @Disabled
    void testPlayerEnter() {

    }

    @Test
    @Disabled
    void testTwoPlayers() {

    }

    @Test
    @Disabled
    void testControllerAndXYOfEachOther() {

    }

}
