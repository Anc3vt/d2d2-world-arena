
package com.ancevt.d2d2world.client.settings;

import com.ancevt.d2d2world.data.file.FileSystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Slf4j
public class ClientConfig {

    public static final ClientConfig CONFIG = new ClientConfig();

    public static final String FILE_NAME = "d2d2-world-arena-desktop.conf";

    public static final String SERVER = "server";
    public static final String PLAYERNAME = "playername";
    public static final String RCON_PASSWORD = "rcon-password";
    public static final String DEBUG_WORLD_ALPHA = "debug.world-alpha";
    public static final String DEBUG_GAME_OBJECT_IDS = "debug.game-object-ids";
    public static final String DEBUG_CHARACTER = "debug.character-mapkit-item";
    public static final String AUTO_ENTER = "auto-enter";
    public static final String DISPLAY_FULLSCREEN = "display.fullscreen";
    public static final String DEBUG_WINDOW_SIZE = "debug.window-size";
    public static final String SOUND_ENABLED = "sound-enabled";
    public static final String DEBUG_WINDOW_XY = "debug.window-xy";
    public static final String DISPLAY_MONITOR = "display.monitor";
    public static final String DISPLAY_RESOLUTION = "display.resolution";

    private static final Map<String, Object> defaults = new TreeMap<>() {{
        put(SERVER, "");
        put(DEBUG_WORLD_ALPHA, "1.0");
        put(DEBUG_GAME_OBJECT_IDS, "false");
        put(AUTO_ENTER, "false");
        put(DISPLAY_FULLSCREEN, "true");
        put(SOUND_ENABLED, "true");
        put(DISPLAY_MONITOR, "primary");
        put(DISPLAY_RESOLUTION, "");
    }};

    private final List<ConfigChangeListener> changeListeners;

    private final Properties properties;

    private ClientConfig() {
        properties = new Properties();
        changeListeners = new ArrayList<>();
    }

    public void ifKeyPresent(String key, Consumer<String> valueConsumer) {
        String value = getString(key);
        if (!value.equals("")) {
            valueConsumer.accept(value);
        }
    }

    public void addConfigChangeListener(ConfigChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeConfigChangeListener(ConfigChangeListener listener) {
        changeListeners.remove(listener);
    }

    public void save() {
        try {
            properties.store(new FileOutputStream("d2d2-world-arena-desktop.conf"), null);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void load() throws IOException {
        properties.clear();
        if (FileSystemUtils.exists(FILE_NAME)) {
            properties.load(FileSystemUtils.getInputStream(FILE_NAME));
            log.info("DesktopConfig loaded");
        } else {
            log.warn("No config file detected, creating defaults");
            createDefault();
        }
    }

    public void setProperty(@NotNull String key, @NotNull Object value) {
        properties.setProperty(key, value.toString());
        changeListeners.forEach(l->l.configPropertyChange(key, value));
    }

    /**
     * Get by key from config or from defaults or ""
     */
    public @NotNull String getString(@NotNull String key) {
        return properties.getProperty(key, String.valueOf(defaults.getOrDefault(key, "")));
    }

    public int getInt(@NotNull String key) {
        try {
            return parseInt(properties.getProperty(key, String.valueOf(defaults.get(key))));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public long getLong(@NotNull String key) {
        try {
            return parseLong(properties.getProperty(key, String.valueOf(defaults.get(key))));
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    public boolean getBoolean(@NotNull String key) {
        return "true".equals(getString(key));
    }

    public float getFloat(@NotNull String key) {
        try {
            return parseFloat(properties.getProperty(key, String.valueOf(defaults.get(key))));
        } catch (NumberFormatException ex) {
            return 0f;
        }
    }

    private void createDefault() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            stringBuilder.append(entry.getKey()).append('=').append(entry.getValue()).append("\r\n");
        }

        try {
            Files.writeString(Path.of(FILE_NAME), stringBuilder.toString(), StandardCharsets.UTF_8, CREATE_NEW);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String passwordSafeToString() {
        return toString().replaceAll(properties.getProperty(RCON_PASSWORD), "*****");
    }

    @Override
    public String toString() {
        return "DesktopConfig{" +
                "properties=" + properties +
                '}';
    }

    @FunctionalInterface
    public interface ConfigChangeListener {
        void configPropertyChange(String key, Object value);
    }
}
