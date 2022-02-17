package ru.ancevt.d2d2.event;

public interface IEventDispatcher {

    void addEventListener(String type, EventListener listener);

    void addEventListener(String type, EventListener listener, boolean reset);

    void removeEventListener(String type, EventListener listener);

    void dispatchEvent(Event event);

    void removeAllEventListeners(String type);

    void removeAllEventListeners();
}
