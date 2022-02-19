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

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.ScaleMode;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.EventListener;
import ru.ancevt.d2d2.event.TouchEvent;
import ru.ancevt.d2d2.input.Clipboard;
import ru.ancevt.d2d2.input.KeyCode;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;
import ru.ancevt.d2d2.touch.TouchButton;

public class UiTextInput extends DisplayObjectContainer implements EventListener {


    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        Root root = D2D2.getStage().getRoot();
        root.setBackgroundColor(new Color(0, 0, 40));

        UiTextInput uiTextInput = new UiTextInput();
        root.add(uiTextInput, 100, 100);

        UiTextInput uiTextInput1 = new UiTextInput();
        root.add(uiTextInput1, 100, 140);

        TextInputProcessor.enableRoot(root);
        D2D2.getStage().setScaleMode(ScaleMode.EXTENDED);
        D2D2.loop();
    }


    public static final int DOWN_DELAY = 30;

    private boolean down;
    private int downKeyCode;
    private int downCounter = DOWN_DELAY;
    private char downKeyChar;
    private boolean shiftDown;
    private boolean controlDown;

    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color SELECTION_COLOR = Color.DARK_GRAY;
    private static final float BACKGROUND_ALPHA = 0.75f;
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 30;

    private final PlainRect background;
    private final PlainRect selection;
    private final TouchButton touchButton;
    private final UiText uiText;
    private final Caret caret;
    private boolean focused;
    private boolean selecting;
    private int selectionFromIndex;
    private int selectionToIndex;
    private int selectionStartIndex;
    private String text;
    private int scroll;

    public UiTextInput() {
        background = new PlainRect(DEFAULT_WIDTH, DEFAULT_HEIGHT, BACKGROUND_COLOR);
        selection = new PlainRect(0, DEFAULT_HEIGHT - 8, SELECTION_COLOR);
        touchButton = new TouchButton(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        add(touchButton);
        uiText = new UiText();
        uiText.setShadowEnabled(false);

        background.setAlpha(BACKGROUND_ALPHA);

        add(background);
        // add(selection, uiText.getX(), 4); // selection is completely not implemented yet
        add(uiText);

        caret = new Caret();
        caret.setXY(uiText.getX(), 4);

        text = "";
        setText(text);

        align();

        addEventListener(Event.ADD_TO_STAGE, this);
        addEventListener(Event.REMOVE_FROM_STAGE, this);
        touchButton.addEventListener(TouchEvent.TOUCH_DOWN, this);
    }

    public void setColor(Color color) {
        uiText.setColor(color);
    }

    public Color getColor() {
        return uiText.getColor();
    }

    public void setSelection(int fromIndex, int toIndex) {
        if (fromIndex < toIndex) {
            int temp = fromIndex;
            fromIndex = toIndex;
            toIndex = temp;
        }

        this.selectionFromIndex = fromIndex;
        this.selectionToIndex = toIndex;

        redrawSelection();
    }

    public void resetSelection() {
        selectionStartIndex = 0;
        selectionFromIndex = 0;
        selectionToIndex = 0;
        redrawSelection();
    }

    private void redrawSelection() {
        selection.setX(uiText.getX() + selectionFromIndex * uiText.getCharWidth());
        selection.setWidth((selectionToIndex - selectionFromIndex) * uiText.getCharWidth());
    }

    public void setText(@NotNull String text) {
        this.text = text;
        this.uiText.setText(text);
        if (getCaretPosition() > text.length()) {
            setCaretPosition(Integer.MAX_VALUE);
        }

        dispatchEvent(new TextInputEvent(TextInputEvent.TEXT_CHANGE, this, getText(), 0));
    }

    public String getText() {
        return text;
    }

    public void setCaretPosition(int index) {
        index = fixIndex(index);
        caret.setX(uiText.getX() + (index * uiText.getCharWidth()) + 1);
        caret.setAlpha(1f);
    }

    private int fixIndex(int index) {
        int len = text.length();

        if (index > len) {
            index = len;
        } else if (index < 0) {
            index = 0;
        }
        return index;
    }

    public int getCaretPosition() {
        return (int) (caret.getX() / uiText.getCharWidth());
    }

    public void dispose() {
        removeEventListener(Event.ADD_TO_STAGE, this);
        removeEventListener(Event.REMOVE_FROM_STAGE, this);
        touchButton.removeEventListener(TouchEvent.TOUCH_DOWN, this);
        touchButton.setEnabled(false);
        removeFromParent();
        TextInputProcessor.INSTANCE.removeTextInput(this);
    }

    public void setWidth(float width) {
        background.setWidth(width);
        touchButton.setWidth(width);
        align();
    }

    public void setHeight(float height) {
        background.setHeight(height);
        touchButton.setHeight(height);
        align();
    }

    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    @Override
    public float getWidth() {
        return background.getWidth();
    }

    @Override
    public float getHeight() {
        return background.getHeight();
    }

    private void align() {
        int alignTop = 10;
        int alignLeft = 5;

        uiText.setXY(alignLeft, alignTop);
        uiText.setWidth(getWidth() - (alignLeft * 2));
        uiText.setHeight(getHeight() - (alignTop * 2));
    }

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {

            case Event.ADD_TO_STAGE -> {
                touchButton.setEnabled(true);
                TextInputProcessor.INSTANCE.addTextInput(this);
                TextInputProcessor.INSTANCE.focus(this);
            }

            case Event.REMOVE_FROM_STAGE -> {
                touchButton.setEnabled(false);
                TextInputProcessor.INSTANCE.removeTextInput(this);
                if (isFocused()) TextInputProcessor.INSTANCE.resetFocus();
            }

            case TouchEvent.TOUCH_DOWN -> {
                TouchEvent touchEvent = (TouchEvent) event;
                TextInputProcessor.INSTANCE.focus(this);
                // TODO: repair caret position when scale mode is extended
                setCaretPosition((int) (touchEvent.getX() / uiText.getCharWidth()));
            }
        }
    }

    public void key(int keyCode, char keyChar, boolean control, boolean shift, boolean alt, boolean down) {
        if (down) {
            this.down = true;
            downKeyCode = keyCode;
            downKeyChar = keyChar;
            shiftDown = shift;
            controlDown = control;
            if (KeyCode.isShift(keyCode)) {
                selecting = true;
                selectionStartIndex = getCaretPosition();
            } else if (!selecting) {
                resetSelection();
            }

            keyDown(keyCode, keyChar, control, shift);
        } else {
            this.down = false;
            if (KeyCode.isShift(keyCode)) {
                selecting = false;
            }
            downKeyCode = 0;
            downCounter = DOWN_DELAY;
            downKeyChar = '\0';
            shiftDown = !KeyCode.isShift(keyCode);
            controlDown = !KeyCode.isControl(keyCode);
        }
    }

    private void keyDown(int keyCode, char keyChar, boolean control, boolean shift) {
        switch (keyCode) {

            case KeyCode.RIGHT -> {
                setCaretPosition(getCaretPosition() + 1);
                if (selecting) {
                    if (getCaretPosition() > selectionStartIndex) {
                        setSelection(selectionStartIndex, getCaretPosition());
                    } else {
                        setSelection(getCaretPosition(), selectionStartIndex);
                    }
                }

            }
            case KeyCode.LEFT -> {
                setCaretPosition(getCaretPosition() - 1);

                if (selecting) {
                    if (getCaretPosition() < selectionStartIndex) {
                        setSelection(selectionStartIndex, getCaretPosition());
                    } else {
                        setSelection(getCaretPosition(), selectionStartIndex);
                    }
                }
            }
            case KeyCode.BACKSPACE -> {
                if (control) removeWord();
                else removeChar();
            }
            case KeyCode.DELETE -> {
                int index = getCaretPosition();
                if (index < text.length()) {
                    setText(text.substring(0, index) + text.substring(index + 1));
                }
            }
            case KeyCode.HOME -> setCaretPosition(0);

            case KeyCode.END -> setCaretPosition(text.length());

            case KeyCode.ENTER -> dispatchEvent(
                    new TextInputEvent(TextInputEvent.TEXT_ENTER, this, getText(), keyCode));
        }

        if (control) {
            switch (keyChar) {
                case 'X' -> {
                    Clipboard.set(getText());
                    setText("");
                    setCaretPosition(getCaretPosition());
                }
                case 'V' -> {
                    insertText(Clipboard.get());
                    setCaretPosition(getCaretPosition() + Clipboard.get().length());
                }
                case 'C' -> {
                    Clipboard.set(getText());
                }
                case 'W' -> {
                    removeWord();
                }
            }
        }

        dispatchEvent(new TextInputEvent(TextInputEvent.TEXT_INPUT_KEY_DOWN, this, getText(), keyCode));
    }

    public void keyType(int codepoint, String keyType) {
        if (!uiText.getBitmapFont().isCharSupported(keyType.charAt(0))) return;
        if (text.length() * uiText.getCharWidth() < getWidth() - 10) insertText(keyType);
    }

    @Override
    public void onEachFrame() {
        if (down) {
            downCounter--;

            if (downCounter <= 0) {
                keyDown(downKeyCode, downKeyChar, controlDown, shiftDown);
                downCounter = 3;
            }
        }
    }

    void focus() {
        this.focused = true;
        add(caret);
    }

    void focusLost() {
        focused = false;
        caret.removeFromParent();
    }

    public boolean isFocused() {
        return focused;
    }

    private void insertText(String textToInsert) {
        if (textToInsert.length() == 0) {
            setText(textToInsert);
        }
        int index = getCaretPosition();
        setText(text.substring(0, index) + textToInsert + text.substring(index));
        setCaretPosition(getCaretPosition() + textToInsert.length());

        while (text.length() * uiText.getCharWidth() > getWidth() - 10) {
            removeChar();
        }
    }

    private void removeChar() {
        int index = getCaretPosition();
        if (index > 0) {
            String newText = text.substring(0, index - 1) + text.substring(index);
            setCaretPosition(getCaretPosition() - 1);
            setText(newText);
        }
    }

    private void removeWord() {
        if (getCaretPosition() == 0) return;

        if (getCharUnderCaret() == ' ') {
            removeChar();
        } else
            while (getCaretPosition() > 0 && getCharUnderCaret() != ' ') {
                removeChar();
            }
    }

    private char getCharUnderCaret() {
        return text.charAt(getCaretPosition() - 1);
    }

    public void clear() {
        setText("");
    }

    public void moveCaretToEnd() {
        setCaretPosition(Integer.MAX_VALUE);
    }

    public void requestFocus() {
        TextInputProcessor.INSTANCE.focus(this);
    }

    private static class Caret extends PlainRect {

        public static final int BLINK_DELAY = 25;

        private int blinkCounter = BLINK_DELAY;

        public Caret() {
            super(1, DEFAULT_HEIGHT - 8, Color.WHITE);
        }

        @Override
        public void onEachFrame() {
            blinkCounter--;
            if (blinkCounter <= 0) {
                blinkCounter = BLINK_DELAY;
                setAlpha(getAlpha() == 1f ? 0f : 1f);
            }
        }
    }

}































