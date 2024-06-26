/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2.panels;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.interactive.InteractiveContainer;

import static com.ancevt.d2d2.D2D2.stage;

public class TextInput extends Component implements EventListener {

    private static final float PADDING = 5;

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Color DISABLED_BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static final Color FOCUSED_BORDER_COLOR = Color.BLUE;
    private static final Color FOREGROUND_COLOR = Color.BLACK;

    private static final float CURSOR_WIDTH = 1;
    private static final float CURSOR_HEIGHT = 14;

    private static final float DEFAULT_WIDTH = 120;
    private static final float DEFAULT_HEIGHT = 20;

    private static final int[] NON_CHARS_KEY_CODES = new int[]{
            10, 16, 17, 18, 19, 20, 157
    };

    private final BorderedRect rect;
    private final BitmapText bitmapText;
    private final PlainRect cursor;
    private final InteractiveContainer touchButton;

    public TextInput() {
        rect = new BorderedRect(BACKGROUND_COLOR, BORDER_COLOR);
        bitmapText = new BitmapText();
        cursor = new PlainRect(CURSOR_WIDTH, CURSOR_HEIGHT, Color.BLACK);

        bitmapText.setColor(FOREGROUND_COLOR);
        bitmapText.setX(PADDING);

        touchButton = new InteractiveContainer();
        touchButton.addEventListener(InteractiveEvent.DOWN, e -> {
            onTouch();
        });

        setEnabled(true);
        add(rect);
        add(bitmapText);
        add(touchButton);

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        addEventListener(Event.ADD_TO_STAGE, this);
    }

    private void relocateCursor() {
        final float newX = bitmapText.getX() + bitmapText.getTextWidth() + 1;

        cursor.setVisible(newX < getWidth());

        if (newX < getWidth())
            cursor.setX(newX);
    }

    @Override
    public void onFocus() {
        rect.setBorderColor(FOCUSED_BORDER_COLOR);
        stage().removeEventListener(this, InputEvent.KEY_DOWN);
        stage().addEventListener(this, InputEvent.KEY_DOWN, this);
        relocateCursor();
        add(cursor);
        super.onFocus();
    }

    @Override
    public void onFocusLost() {
        rect.setBorderColor(BORDER_COLOR);
        remove(cursor);
        stage().removeEventListener(this, InputEvent.KEY_DOWN);
        super.onFocusLost();
    }

    public final void onTouch() {
        Focus.setFocusedComponent(this);
    }

    public void setText(String text) {
        bitmapText.setText(text);
    }

    public String getText() {
        return bitmapText.getText();
    }

    public void setWidth(float width) {
        rect.setWidth(width);
        bitmapText.setWidth(width);
        touchButton.setWidth((int) width);
    }

    public void setHeight(float height) {
        rect.setHeight(height);

        bitmapText.setHeight(height - bitmapText.getBitmapFont().getZeroCharHeight());
        bitmapText.setY((getHeight() - bitmapText.getBitmapFont().getZeroCharHeight()) / 2);
        cursor.setY((getHeight() - cursor.getHeight()) / 2);
        touchButton.setHeight((int) height);
    }

    @Override
    public float getWidth() {
        return rect.getWidth();
    }

    @Override
    public float getHeight() {
        return rect.getHeight();
    }

    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    public void setEnabled(boolean enabled) {
        touchButton.setEnabled(enabled);
        rect.setFillColor(enabled ? BACKGROUND_COLOR : DISABLED_BACKGROUND_COLOR);
    }

    public boolean isEnabled() {
        return touchButton.isEnabled();
    }

    public void onTextEnter() {

    }

    public void onTextChange() {

    }

    @Override
    public void dispose() {
        super.dispose();
        stage().removeEventListener(this, InputEvent.KEY_DOWN);
    }

    private static boolean isNonChar(int keyCode) {
        for (final int k : NON_CHARS_KEY_CODES) {
            if (k == keyCode) return true;
        }
        return false;
    }

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {
            case InputEvent.KEY_DOWN -> {
                InputEvent inputEvent = (InputEvent) event;

                int keyCode = inputEvent.getKeyCode();
                char keyChar = inputEvent.getKeyChar();

                if (keyCode == 10) {
                    onTextEnter();
                    Focus.setFocusedComponent(null);
                }

                if (keyCode == '\0' || isNonChar(keyCode)) return;

                if (keyCode == 259) {
                    if (bitmapText.getText().length() == 0) return;
                    bitmapText.setText(bitmapText.getText().substring(0, bitmapText.getText().length() - 1));
                } else {
                    bitmapText.setText(bitmapText.getText() + shift(keyChar, inputEvent.isShift()));
                }
                relocateCursor();
                onTextChange();
            }
            case Event.ADD_TO_STAGE -> {
                removeEventListener(Event.ADD_TO_STAGE, this);
            }
        }
    }

    private static char shift(char c, boolean shift) {
        if (shift) {
            final char[] chars = new char[]{
                    '0', ')',
                    '1', '!',
                    '2', '@',
                    '3', '#',
                    '4', '$',
                    '5', '%',
                    '6', '^',
                    '7', '&',
                    '8', '*',
                    '9', '(',
                    '`', '~',
                    '-', '_',
                    '=', '+',
                    ',', '<',
                    '.', '>',
                    '/', '?',
                    ';', ':',
                    '\'', '"',
                    '[', '{',
                    ']', '}',
                    '\\', '|'
            };

            for (int i = 0; i < chars.length; i+=2) {
                char current = chars[i];
                char value = chars[i + 1];
                if (c == current) return String.valueOf(value).toLowerCase().charAt(0);
            }

            return c;
        }

        return String.valueOf(c).toLowerCase().charAt(0);
    }

}



















