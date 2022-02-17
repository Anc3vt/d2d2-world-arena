package ru.ancevt.d2d2world.server;

@FunctionalInterface
public interface GlobalTimerListener {

    void globalTimerTick(long count);
}
