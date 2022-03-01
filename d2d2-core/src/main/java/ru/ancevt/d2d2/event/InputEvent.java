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

public class InputEvent extends Event {

    public static final String BACK_PRESS = "backPress";
    public static final String MOUSE_MOVE = "mouseMove";
    public static final String MOUSE_DOWN = "mouseDown";
    public static final String MOUSE_UP = "mouseUp";
    public static final String MOUSE_WHEEL = "mouseWheel";
    public static final String KEY_DOWN = "keyDown";
    public static final String KEY_UP = "keyUp";
    public static final String KEY_TYPE = "keyType";

    private final int x;
    private final int y;
    private final int mouseButton;
    private final int delta;
    private final boolean drag;
    private final int pointer;
    private final int keyCode;
    private final char keyChar;
    private final boolean shift;
    private final boolean control;
    private final boolean alt;
    private final String keyType;
    private final int codepoint;

    public InputEvent(String type,
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

        super(type, source);

        this.x = x;
        this.y = y;
        this.mouseButton = mouseButton;
        this.delta = delta;
        this.drag = drag;
        this.pointer = pointer;
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.shift = shift;
        this.control = control;
        this.alt = alt;
        this.codepoint = 0;
        this.keyType = "";
    }

    public InputEvent(String type,
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

        super(type, source);

        this.x = x;
        this.y = y;
        this.mouseButton = mouseButton;
        this.delta = delta;
        this.drag = drag;
        this.pointer = pointer;
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.shift = shift;
        this.control = control;
        this.alt = alt;
        this.codepoint = codepoint;
        this.keyType = keyType;
    }

    public int getCodepoint() {
        return codepoint;
    }

    public String getKeyType() {
        return keyType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMouseButton() {
        return mouseButton;
    }

    public int getDelta() {
        return delta;
    }

    public boolean isDrag() {
        return drag;
    }

    public int getPointer() {
        return pointer;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public char getKeyChar() {
        return keyChar;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isControl() {
        return control;
    }

    public boolean isAlt() {
        return alt;
    }

    @Override
    public String toString() {
        return "InputEvent{" +
                "x=" + x +
                ", y=" + y +
                ", mouseButton=" + mouseButton +
                ", delta=" + delta +
                ", drag=" + drag +
                ", pointer=" + pointer +
                ", keyCode=" + keyCode +
                ", keyChar=" + keyChar +
                ", shift=" + shift +
                ", control=" + control +
                ", alt=" + alt +
                '}';
    }
}
