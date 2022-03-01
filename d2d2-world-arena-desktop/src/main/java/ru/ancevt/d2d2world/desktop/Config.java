/*
 *   D2D2 World Arena Desktop
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
package ru.ancevt.d2d2world.desktop;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static java.lang.Integer.parseInt;

@Slf4j
public class Config {

    public static final String FILE_NAME = "d2d2-world-arena-desktop.conf";

    public static final String SERVER = "server";
    public static final String PLAYER = "player";
    public static final String RCON_PASSWORD = "rcon-password";

    private final Properties properties;

    private static final String[] defaults = new String[]{
            SERVER, "ancevt.ru:2245"
    };

    public Config() {
        properties = new Properties();
    }

    public void load() throws IOException {


        properties.clear();
        File file = new File(FILE_NAME);
        if (file.exists()) {
            properties.load(new FileInputStream(file));
            log.info("Config loaded");
        } else {
            log.warn("No config file detected");
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
