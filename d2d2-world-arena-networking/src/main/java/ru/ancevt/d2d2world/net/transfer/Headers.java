package ru.ancevt.d2d2world.net.transfer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Headers {

    private final Map<String, String> map;

    private Headers(String headers) {
        this();
        parse(headers);
    }

    private void parse(String headers) {
        String[] lines = headers.split("\n");
        for (String line : lines) {
            String[] kv = line.split(":");
            if (kv.length < 2) {
                throw new IllegalArgumentException("wrong headers line: " + line);
            }
            put(kv[0], kv[1]);
        }
    }

    private Headers() {
        map = new HashMap<>();
    }


    public Headers put(String key, String value) {
        map.put(key, value);
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

    public Optional<String> get(String key) {
        return Optional.ofNullable(map.get(key));
    }

    public static Headers of(String string) {
        return new Headers(string);
    }

    public static Headers newHeaders() {
        return new Headers();
    }
}
