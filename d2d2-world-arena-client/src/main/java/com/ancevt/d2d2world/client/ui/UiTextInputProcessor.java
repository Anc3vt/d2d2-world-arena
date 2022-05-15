
package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class UiTextInputProcessor {

    public static UiTextInputProcessor INSTANCE = new UiTextInputProcessor();
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

    private UiTextInputProcessor() {
        uiTextInputs = new ArrayList<>();
    }

    public static void enableRoot(Root root) {
        Root oldRoot = UiTextInputProcessor.root;
        UiTextInputProcessor.root = root;

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

    public void unfocus() {
        uiTextInputs.forEach(UiTextInput::focusLost);
        resetFocus();
    }

    public void focus(UiTextInput uiTextInput) {
        uiTextInputs.stream().filter(t -> t != uiTextInput).forEach(UiTextInput::focusLost);
        uiTextInput.setCaretPosition(Integer.MAX_VALUE);
        uiTextInput.focus();

        index = uiTextInputs.indexOf(uiTextInput);
    }

}
