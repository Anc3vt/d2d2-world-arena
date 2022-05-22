
package com.ancevt.d2d2.event;

import com.ancevt.d2d2.display.IDisplayObjectContainer;

public class EventPool {

    private static final Event SIMPLE_EVENT_SINGLETON = Event.builder().build();

    public static Event createEvent(String type, IDisplayObjectContainer parent) {
        return Event.builder().type(type).parent(parent).build();
    }

    public static Event createEvent(String type) {
        return createEvent(type, null);
    }

    public static TouchButtonEvent createTouchButtonEvent(String type, int x, int y, int button, boolean onArea) {
        return TouchButtonEvent.builder()
                .type(type)
                .x(x)
                .y(y)
                .mouseButton(button)
                .onArea(onArea)
                .build();
    }

    public static Event simpleEventSingleton(String type, IEventDispatcher source) {
        SIMPLE_EVENT_SINGLETON.type = type;
        SIMPLE_EVENT_SINGLETON.source = source;
        return SIMPLE_EVENT_SINGLETON;
    }
}
