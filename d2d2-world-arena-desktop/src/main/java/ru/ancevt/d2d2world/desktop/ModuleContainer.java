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
