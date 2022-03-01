/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ru.ancevt.d2d2.event;

import ru.ancevt.d2d2.display.IDisplayObjectContainer;

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
