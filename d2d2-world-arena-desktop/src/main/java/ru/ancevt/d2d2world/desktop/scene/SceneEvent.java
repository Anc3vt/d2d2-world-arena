package ru.ancevt.d2d2world.desktop.scene;

import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.IEventDispatcher;

public class SceneEvent extends Event {

    public static final String MAP_LOADED = "mapLoaded";

    public SceneEvent(String type, IEventDispatcher source) {
        super(type, source);
    }
}
