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
    public static final String SERVER_CONNECTION_TIMEOUT = "server.connection-timeout";
    public static final String SERVER_LOOP_DELAY = "server.loop-delay";
    public static final String SERVER_MAX_PLAYERS = "world.max-players";
    public static final String RCON_PASSWORD = "rcon.password";
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
            log.info("Config loaded {}", passwordSafeToString());
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

    public String passwordSafeToString() {
        return toString().replaceAll(properties.getProperty(RCON_PASSWORD), "*****");
    }

    @Override
    public String toString() {
        return "Config{" +
                "properties=" + properties +
                '}';
    }


}

