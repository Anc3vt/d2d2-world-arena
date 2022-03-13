/*
 *   D2D2 World Arena Server
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
package com.ancevt.d2d2world.server;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Slf4j
public class ServerConfig {

    public static final ServerConfig MODULE_SERVER_CONFIG = new ServerConfig();

    private static final String FILE_NAME = "d2d2-world-arena-server.conf";

    public static final String SERVER_NAME = "server.name";
    public static final String SERVER_HOST = "server.host";
    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_CONNECTION_TIMEOUT = "server.connection-timeout";
    public static final String SERVER_LOOP_DELAY = "server.loop-delay";
    public static final String SERVER_MAX_PLAYERS = "world.max-players";
    public static final String RCON_PASSWORD = "rcon.password";
    public static final String WORLD_DEFAULT_MAP = "world.default-map";
    public static final String WORLD_DEFAULT_MOD = "world.default-mod";
    public static final String CONTENT_COMPRESSION = "content.compression";

    private static final Map<String, Object> defaults = new TreeMap<>() {{
        put(SERVER_NAME, "D2D2 World Arena Server");
        put(SERVER_HOST, "0.0.0.0");
        put(SERVER_PORT, 2245);
        put(SERVER_CONNECTION_TIMEOUT, 5 * 1000);
        put(SERVER_LOOP_DELAY, 1);
        put(SERVER_MAX_PLAYERS, 100);
        put(RCON_PASSWORD, "changeme");
        put(WORLD_DEFAULT_MAP, "map0.wam");
        put(WORLD_DEFAULT_MOD, "mod0.js");
        put(CONTENT_COMPRESSION, "true");
    }};

    private final Properties properties;

    private ServerConfig() {
        properties = new Properties();
    }

    public void load() throws IOException {
        properties.clear();
        File file = new File(FILE_NAME);
        if (file.exists()) {
            properties.load(new FileInputStream(file));
            log.info("ServerConfig loaded {}", passwordSafeToString());
        } else {
            log.warn("No config file detected, creating defaults");
            createDefault();
        }
    }

    public void setProperty(@NotNull String key, @NotNull Object value) {
        properties.setProperty(key, value.toString());
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

    public boolean getBoolean(@NotNull String key) {
        return parseBoolean(getString(key));
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
        return "ServerConfig{" +
                "properties=" + properties +
                '}';
    }
}
