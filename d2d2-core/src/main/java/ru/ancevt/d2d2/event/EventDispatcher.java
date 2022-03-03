/*
 *   D2D2 core
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
package ru.ancevt.d2d2.event;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventDispatcher implements IEventDispatcher {

    private final Map<String, List<EventListener>> map;

    /**
     * ref to 'map' above
     * key : type
     */
    private final Map<Object, String> keysTypes;

    public EventDispatcher() {
        map = new HashMap<>();
        keysTypes = new HashMap<>();
    }

    @Override
    public void addEventListener(String type, EventListener listener) {
        List<EventListener> listeners = map.getOrDefault(type, createList());
        listeners.add(listener);
        map.put(type, listeners);
    }

    @Override
    public void addEventListener(String type, EventListener listener, boolean reset) {
        if (reset) removeEventListener(type, listener);
        addEventListener(type, listener);
    }

    @Override
    public void addEventListener(Object key, String type, EventListener listener) {
        addEventListener(type, listener);
        keysTypes.put(key, type);
    }

    /**
     * You may remove listeners by given 'key'
     */
    @Override
    public void addEventListener(Object key, String type, EventListener listener, boolean reset) {
        addEventListener(type, listener, reset);
        keysTypes.put(key, type);
    }

    private @NotNull List<EventListener> createList() {
        return new CopyOnWriteArrayList<>();
    }

    @Override
    public void removeEventListener(String type, EventListener listener) {
        List<EventListener> listeners = map.get(type);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void removeEventListeners(Object key) {
        String type = keysTypes.remove(key);
        if (type != null) {
            List<EventListener> listeners = map.get(type);
            if (listeners != null) {
                listeners.clear();
            }
        }
    }

    @Override
    public void dispatchEvent(@NotNull Event event) {
        List<EventListener> listeners = map.get(event.getType());

        if (listeners != null) {
            listeners.forEach(e -> e.onEvent(event));
        }
    }

    @Override
    public void removeAllEventListeners(String type) {
        map.remove(type);
    }

    @Override
    public void removeAllEventListeners() {
        map.clear();
    }
}
