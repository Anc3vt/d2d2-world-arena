package ru.ancevt.d2d2.event;

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
        if (reset) removeEventListeners(type, listener);
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

    private List<EventListener> createList() {
        return new CopyOnWriteArrayList<>();
    }

    @Override
    public void removeEventListeners(String type, EventListener listener) {
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
    public void dispatchEvent(Event event) {
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
