package ru.ancevt.d2d2world.desktop;

import java.util.HashMap;
import java.util.Map;

public class ModuleContainer {

    public static final ModuleContainer INSTANCE = new ModuleContainer();

    private final Map<String, Object> modules = new HashMap<>();

    private ModuleContainer() {

    }

    public void addModule(Object module) {
        if (modules.containsKey(module.getClass().getName())) {
            throw new IllegalStateException("module " + module.getClass().getName() + " already exists");
        }
        modules.put(module.getClass().getName(), module);
    }

    public <T> T getModule(Class<T> clazz) {
        return (T) modules.get(clazz.getName());
    }
}
