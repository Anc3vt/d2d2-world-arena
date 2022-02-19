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
package ru.ancevt.d2d2world.desktop.ui;

import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.event.EventListener;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class TextInputProcessor {

    public static TextInputProcessor INSTANCE = new TextInputProcessor();
    private static Root root;
    private static EventListener inputEventListener = event -> {
        if (event instanceof InputEvent inputEvent) {
            switch (event.getType()) {
                case InputEvent.KEY_TYPE -> {
                    INSTANCE.keyType(inputEvent.getCodepoint(), inputEvent.getKeyType());
                }
                case InputEvent.KEY_UP, InputEvent.KEY_DOWN -> {
                    INSTANCE.key(
                            inputEvent.getKeyCode(),
                            inputEvent.getKeyChar(),
                            inputEvent.isControl(),
                            inputEvent.isShift(),
                            inputEvent.isAlt(),
                            event.getType().equals(InputEvent.KEY_DOWN)
                    );
                }
            }
        }
    };

    private final List<UiTextInput> uiTextInputs;
    private int index;

    private TextInputProcessor() {
        uiTextInputs = new ArrayList<>();
    }

    public static void enableRoot(Root root) {
        Root oldRoot = TextInputProcessor.root;
        TextInputProcessor.root = root;

        root.addEventListener(InputEvent.KEY_DOWN, inputEventListener);
        root.addEventListener(InputEvent.KEY_UP, inputEventListener);
        root.addEventListener(InputEvent.KEY_TYPE, inputEventListener);

        if (oldRoot != null) {
            oldRoot.removeEventListener(InputEvent.KEY_DOWN, inputEventListener);
            oldRoot.removeEventListener(InputEvent.KEY_UP, inputEventListener);
            oldRoot.removeEventListener(InputEvent.KEY_TYPE, inputEventListener);
        }
    }

    public void resetFocus() {
        index = 0;
    }

    public void addTextInput(UiTextInput t) {
        uiTextInputs.add(t);
    }

    public void removeTextInput(UiTextInput t) {
        uiTextInputs.remove(t);
    }

    public void key(int code, char chr, boolean control, boolean shift, boolean alt, boolean down) {
        if (down) {
            if (code == KeyCode.TAB) {
                if (shift) focusPrev();
                else focusNext();
            }
        }

        uiTextInputs.stream().filter(UiTextInput::isFocused).findAny().ifPresent(
                t -> t.key(code, chr, control, shift, alt, down)
        );
    }

    public void keyType(int codepoint, String keyType) {
        uiTextInputs.stream().filter(UiTextInput::isFocused).findAny().ifPresent(
                t -> t.keyType(codepoint, keyType)
        );
    }

    public void focusPrev() {
        if (uiTextInputs.size() <= 1) return;

        index--;
        if (index < 0) index = uiTextInputs.size() - 1;

        focus(uiTextInputs.get(index));
    }

    public void focusNext() {
        if (uiTextInputs.size() <= 1) return;

        index++;
        if (index >= uiTextInputs.size()) index = 0;

        focus(uiTextInputs.get(index));
    }

    public void focus(UiTextInput uiTextInput) {
        uiTextInputs.stream().filter(t -> t != uiTextInput).forEach(UiTextInput::focusLost);
        uiTextInput.setCaretPosition(Integer.MAX_VALUE);
        uiTextInput.focus();

        index = uiTextInputs.indexOf(uiTextInput);
    }

}
