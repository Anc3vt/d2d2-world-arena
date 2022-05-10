/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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

public interface IEventDispatcher {

    void addEventListener(String type, EventListener listener);

    void addEventListener(String type, EventListener listener, boolean reset);

    //void addEventListenerByKey(Object key, String type, EventListener listener);

    //void addEventListenerByKey(Object key, String type, EventListener listener, boolean reset);

    void addEventListener(Object owner, String type, EventListener listener);

    void addEventListener(Object owner, String type, EventListener listener, boolean reset);

    void removeEventListener(String type, EventListener listener);

    //void removeEventListenerByKey(Object key);

    void removeEventListener(Object owner, String type);

    void dispatchEvent(Event<?> event);

    void removeAllEventListeners(String type);

    void removeAllEventListeners();
}
