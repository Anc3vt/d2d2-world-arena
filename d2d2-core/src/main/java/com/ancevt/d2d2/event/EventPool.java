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
package com.ancevt.d2d2.event;

import com.ancevt.d2d2.display.IDisplayObjectContainer;

public class EventPool {

    private static final Event SIMPLE_EVENT_SINGLETON = Event.builder().build();

    public static Event createEvent(String type, IEventDispatcher source, IDisplayObjectContainer parent) {
        return Event.builder().type(type).source(source).parent(parent).build();
    }

    public static Event createEvent(String type, IEventDispatcher source) {
        return createEvent(type, source, null);
    }

    public static TouchEvent createTouchEvent(String type, IEventDispatcher source, int x, int y, boolean onArea) {
        return TouchEvent.builder()
                .type(type)
                .source(source)
                .x(x)
                .y(y)
                .onArea(onArea)
                .build();
    }

    public static Event simpleEventSingleton(String type, IEventDispatcher source) {
        SIMPLE_EVENT_SINGLETON.type = type;
        SIMPLE_EVENT_SINGLETON.source = source;
        return SIMPLE_EVENT_SINGLETON;
    }
}
