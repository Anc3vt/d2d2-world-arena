package ru.ancevt.d2d2.event;

public interface IEventDispatcher {

    void addEventListener(String type, EventListener listener);

    void addEventListener(String type, EventListener listener, boolean reset);

    void addEventListener(Object key, String type, EventListener listener);

    void addEventListener(Object key, String type, EventListener listener, boolean reset);

    void removeEventListeners(String type, EventListener listener);

    void removeEventListeners(Object key);

    void dispatchEvent(Event event);

    void removeAllEventListeners(String type);

    void removeAllEventListeners();
}
