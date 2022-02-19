package ru.ancevt.d2d2world.server;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static java.lang.Integer.parseInt;

@Slf4j
public class Config {

    private static final String FILE_NAME = "d2d2-world-arena-server.conf";

    public static final String SERVER_NAME = "server.name";
    public static final String SERVER_HOST = "server.host";
    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_LOOP_DELAY = "server.loop-delay";
    public static final String RCON_PASSWORD = "rcon.password";
    public static final String WORLD_MAX_PLAYERS = "world.max-players";
    public static final String WORLD_DEFAULT_MAP = "world.default-map";
    public static final String WORLD_DEFAULT_MOD = "world.default-mod";

    private final Properties properties;

    public Config() {
        properties = new Properties();
    }

    public void load() throws IOException {
        properties.clear();
        File file = new File(FILE_NAME);
        if (file.exists()) {
            properties.load(new FileInputStream(file));
        }
    }

    public void setProperty(@NotNull String key, @NotNull Object value) {
        properties.setProperty(key, value.toString());
    }

    public String getString(@NotNull String key, @NotNull String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getString(@NotNull String key) {
        return properties.getProperty(key);
    }

    public int getInt(@NotNull String key, int defaultValue) {
        return parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    public int getInt(@NotNull String key) {
        try {
            return parseInt(properties.getProperty(key));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "properties=" + properties +
                '}';
    }


}

