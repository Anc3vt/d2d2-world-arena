package ru.ancevt.d2d2world.desktop;

import java.util.HashMap;
import java.util.Map;

public class ModuleContainer {

    public static final ModuleContainer modules = new ModuleContainer();

    private final Map<String, Object> map = new HashMap<>();

    private ModuleContainer() {
    }

    public void add(String name, Object module) {
        if (map.containsKey(name)) {
            throw new IllegalStateException("module " + name + " already exists");
        }
        map.put(name, module);
    }

    public void add(Object module) {
        if (map.containsKey(module.getClass().getName())) {
            throw new IllegalStateException("module " + module.getClass().getName() + " already exists");
        }
        map.put(module.getClass().getName(), module);
    }

    public <T> T get(Class<T> clazz) {
        return (T) map.get(clazz.getName());
    }

    public <T> T get(String name) {
        return (T) map.get(name);
    }
}
