package ru.ancevt.d2d2.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventDispatcher implements IEventDispatcher {

    private final Map<String, List<EventListener>> map;

    public EventDispatcher() {
        map = new HashMap<>();
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

    private List<EventListener> createList() {
        return new CopyOnWriteArrayList<>();
    }

    @Override
    public void removeEventListener(String type, EventListener listener) {
        List<EventListener> listeners = map.get(type);
        if(listeners != null) {
            listeners.remove(listener);
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
