/*
 *   D2D2 World Arena Desktop
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
package ru.ancevt.d2d2world.desktop.ui.chat;

import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.IEventDispatcher;

public class ChatEvent extends Event {

    public static final String CHAT_TEXT_ENTER = "chatTextEnter";
    public static String CHAT_INPUT_OPEN = "chatInputOpen";
    public static String CHAT_INPUT_CLOSE = "chatInputClose";

    private final String text;

    public ChatEvent(String type, IEventDispatcher source, String text) {
        super(type, source);

        this.text = text;
    }

    public String getText() {
        return text;
    }
}
