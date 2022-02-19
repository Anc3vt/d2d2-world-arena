package ru.ancevt.d2d2world.server;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ModuleContainer {

    public static final ModuleContainer modules = new ModuleContainer();

    private final Map<String, Object> map = new HashMap<>();

    private ModuleContainer() {
    }

    public void add(@NotNull Object module) {
        if (map.containsKey(module.getClass().getName())) {
            throw new IllegalStateException("module " + module.getClass().getName() + " already exists");
        }
        map.put(module.getClass().getName(), module);
    }

    public @NotNull <T> T get(@NotNull Class<T> clazz) {
        T result = (T) map.get(clazz.getName());
        if (result == null) throw new NullPointerException();
        return result;
    }

}
