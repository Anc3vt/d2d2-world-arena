package ru.ancevt.d2d2world.desktop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static java.lang.Integer.parseInt;

public class Config {

    public static final String FILE_NAME = "d2d2-world-arena-desktop.conf";

    public static final String SERVER_ADDRESS = "server";
    public static final String PLAYER_NAME = "player";
    public static final String RCON_PASSWORD = "rcon-password";

    private final Properties properties;

    public Config() {
        ModuleContainer.INSTANCE.addModule(this);
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
