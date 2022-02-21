package ru.ancevt.d2d2world.net.transfer;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Headers {

    private static final String DELIMITER = ":";

    public static final String FILE_BEGIN = "File-Begin"; // true, false
    public static final String PART_NUMBER = "PartNumber"; // from 0
    public static final String PATH = "Path"; // f.e.: <mapkit-uid>/index.mk
    public static final String FINAL_PART = "Final-Part"; // true

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
            if(line.contains(DELIMITER)) {
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
