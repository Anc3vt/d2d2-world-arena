package ru.ancevt.d2d2.event;

@FunctionalInterface
public interface EventListener {

    void onEvent(Event event);
}
