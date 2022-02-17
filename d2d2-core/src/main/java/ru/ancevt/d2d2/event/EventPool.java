package ru.ancevt.d2d2.event;

import ru.ancevt.d2d2.display.IDisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;

public class EventPool {

    private static final Event SIMPLE_EVENT_SINGLETON = new Event(null, null);

    public static Event createEvent(String type, IEventDispatcher source, IDisplayObjectContainer parent) {
        return new Event(type, source, parent);
    }

    public static Event createEvent(String type, IEventDispatcher source) {
        return createEvent(type, source, null);
    }

    public static InputEvent createInputEvent(String type,
                                              IEventDispatcher source,
                                              int x,
                                              int y,
                                              int mouseButton,
                                              int delta,
                                              boolean drag,
                                              int pointer,
                                              int keyCode,
                                              char keyChar,
                                              boolean shift,
                                              boolean control,
                                              boolean alt) {

        return new InputEvent(type, source, x, y, mouseButton, delta, drag, pointer, keyCode, keyChar, shift, control, alt);
    }

    public static InputEvent createInputEvent(String type,
                                              IEventDispatcher source,
                                              int x,
                                              int y,
                                              int mouseButton,
                                              int delta,
                                              boolean drag,
                                              int pointer,
                                              int keyCode,
                                              char keyChar,
                                              boolean shift,
                                              boolean control,
                                              boolean alt,
                                              int codepoint,
                                              String keyType) {

        return new InputEvent(type, source, x, y, mouseButton, delta, drag, pointer, keyCode, keyChar, shift, control, alt, codepoint, keyType);


    }

    public static TouchEvent createTouchEvent(String type,
                                              IEventDispatcher source,
                                              int x,
                                              int y,
                                              boolean onArea) {

        return new TouchEvent(type, source, x, y, onArea);
    }

    public static Event simpleEventSingleton(String type, IEventDispatcher source) {
        SIMPLE_EVENT_SINGLETON.type = type;
        SIMPLE_EVENT_SINGLETON.source = source;
        return SIMPLE_EVENT_SINGLETON;
    }
}
