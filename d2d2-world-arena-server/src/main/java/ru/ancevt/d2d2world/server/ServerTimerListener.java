package ru.ancevt.d2d2world.server;

@FunctionalInterface
public interface ServerTimerListener {

    void globalTimerTick(long count);
}
