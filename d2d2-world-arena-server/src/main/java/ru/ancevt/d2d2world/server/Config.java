package ru.ancevt.d2d2world.server;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class Config {

    private static final Properties properties = new Properties();
    private static final String FILE_NAME = "d2d2-world-arena-server.conf";
    public static final Config INSTANCE = new Config();

    private Config() {
        load();
    }

    public void load() {
        properties.clear();
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                properties.load(new FileInputStream(file));
                log.info("Loaded server config");
            } else {
                log.info("Config file {} not found", FILE_NAME);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String serverName() {
        return properties.getProperty("server.name", "D2D2 World Arena Server");
    }

    public int serverPort() {
        return Integer.parseInt(properties.getProperty("server.port", "2245"));
    }

    public String serverHost() {
        return properties.getProperty("server.host", "0.0.0.0");
    }

    public String rconPassword() {
        return properties.getProperty("rcon.password");
    }

    public int worldMaxPlayers() {
        return Integer.parseInt(properties.getProperty("world.max-players", "100"));
    }

    public String worldDefaultMap() {
        return properties.getProperty("world.default-map", "map0.wam");
    }

    public String worldDefaultMod() {
        return properties.getProperty("world.default-mod", "mod0.js");
    }

    public int getLoopDelay() {
        return Integer.parseInt(properties.getProperty("server.loop-delay", "1"));
    }
}

