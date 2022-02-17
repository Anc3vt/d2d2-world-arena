package ru.ancevt.d2d2.event;

import java.io.IOException;

@FunctionalInterface
public interface EventListener {

    void onEvent(Event event);
}
