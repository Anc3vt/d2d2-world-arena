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

public class Event {

    public static final String EACH_FRAME = "eachFrame";
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String ADD_TO_STAGE = "addToStage";
    public static final String REMOVE_FROM_STAGE = "removeFromStage";
    public static final String COMPLETE = "complete";
    public static final String RESIZE = "resize";
    public static final String CHANGE = "change";

    String type;
    IEventDispatcher source;
    private IDisplayObjectContainer parent;

    public Event(String type, IEventDispatcher source) {
        this.type = type;
        this.source = source;
    }

    public Event(String type, IEventDispatcher source, IDisplayObjectContainer parent) {
        this(type, source);
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    public IEventDispatcher getSource() {
        return source;
    }

    public IDisplayObjectContainer getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" + type + '\'' +
                ", source=" + source +
                ", parent=" + parent +
                '}';
    }
}
