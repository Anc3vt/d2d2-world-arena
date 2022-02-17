/*
 *   D2D2 World Desktop
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
package ru.ancevt.d2d2world.game.ui;

import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.IEventDispatcher;

public class TextInputEvent extends Event {

    public static final String TEXT_CHANGE = "textChange";
    public static final String TEXT_ENTER = "textEnter";
    public static final String TEXT_INPUT_KEY_DOWN = "textInputKeyDown";

    private final String text;
    private final int keyCode;

    public TextInputEvent(String type, IEventDispatcher source, String text, int keyCode) {
        super(type, source);
        this.text = text;
        this.keyCode = keyCode;
    }

    public String getText() {
        return text;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
