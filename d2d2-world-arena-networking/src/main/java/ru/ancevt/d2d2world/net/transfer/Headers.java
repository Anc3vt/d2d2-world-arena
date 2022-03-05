/*
 *   D2D2 World Arena Networking
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
package ru.ancevt.d2d2world.net.transfer;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    private static final String DELIMITER = ":";
    public static final String BEGIN = "Begin";
    public static final String PATH = "Path"; // mapkits/<mapkit-uid>/index.mk, e.t.c.
    public static final String SIZE = "Size"; // bytes

    private final Map<String, String> map;

    private Headers(String headers) {
        this();
        parse(headers);
    }

    private Headers() {
        map = new HashMap<>();
    }

    private void parse(String headers) {
        String[] lines = headers.split("\n");
        for (String line : lines) {
            if (line.contains(DELIMITER)) {
                String[] kv = line.split(DELIMITER);
                put(kv[0], kv[1]);
            } else {
                put(line);
            }
        }
    }

    public Headers put(@NotNull String key, @NotNull String value) {
        map.put(key, value);
        return this;
    }

    public Headers put(@NotNull String key) {
        map.put(key, null);
        return this;
    }

    public Headers remove(String key) {
        map.remove(key);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        map.forEach((k, v) -> {
            s.append(k).append(":").append(v).append('\n');
        });
        return s.toString();
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public String get(String key) {
        if (!contains(key)) {
            throw new IllegalStateException("no suck key '" + key + "'");
        }
        return map.get(key);
    }

    public static Headers of(String string) {
        return new Headers(string);
    }

    public static Headers newHeaders() {
        return new Headers();
    }
}
