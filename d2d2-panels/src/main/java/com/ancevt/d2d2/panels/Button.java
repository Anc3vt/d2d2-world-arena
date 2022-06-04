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

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.InteractiveEvent;
import com.ancevt.d2d2.interactive.InteractiveContainer;

public class Button extends Panel {

    private static final int PADDING = 8;
    private static final String DEFAULT_BUTTON_LABEL_TEXT = "Button";
    private static final float DEFAULT_WIDTH = 120;
    private static final float DEFAULT_HEIGHT = 30;
    private static final Color DISABLED_COLOR = Color.GRAY;

    private final BitmapText label;
    private final InteractiveContainer touchButton;
    private Sprite icon;
    private boolean pressed;

    public Button() {
        this(DEFAULT_BUTTON_LABEL_TEXT);
    }

    public Button(String labelText) {
        label = new BitmapText();
        label.setColor(Color.BLACK);
        label.setText(labelText);

        touchButton = new InteractiveContainer();

        touchButton.addEventListener(InteractiveEvent.DOWN, e -> {
            Focus.setFocusedComponent(this);

            borderLeft.setColor(BORDER_COLOR_2);
            borderRight.setColor(BORDER_COLOR_1);
            borderTop.setColor(BORDER_COLOR_2);
            borderBottom.setColor(BORDER_COLOR_1);
            label.move(1, 1);
            pressed = true;
        });

        touchButton.addEventListener(InteractiveEvent.UP, event -> {
            var e = (InteractiveEvent) event;

            borderLeft.setColor(BORDER_COLOR_1);
            borderRight.setColor(BORDER_COLOR_2);
            borderTop.setColor(BORDER_COLOR_1);
            borderBottom.setColor(BORDER_COLOR_2);
            label.move(-1, -1);

            if (!pressed) return;
            pressed = false;

            if(e.isOnArea()) onButtonPressed();
        });

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        add(touchButton);
        touchButton.setEnabled(true);
        add(label);
        setEnabled(true);
    }

    public void setColor(Color color) {
        label.setColor(color);
    }

    public Color getColor() {
        return label.getColor();
    }

    public void onButtonPressed() {

    }

    public void setText(String text) {
        label.setText(text);
    }

    public String getText() {
        return label.getText();
    }

    public void setBackgroundColor(Color color) {
        background.setColor(color);
    }

    public Color getBackgroundColor() {
        return background.getColor();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);

        if (label != null && touchButton != null) {
            final int charHeight = label.getBitmapFont().getCharHeight();
            label.setSize(width - PADDING * 2, charHeight);
            label.setX(PADDING);
            touchButton.setWidth(width);
        }

        if (icon != null) {
            icon.setX((width - icon.getWidth() * icon.getAbsoluteScaleX()) / 2);
        }
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);

        if (label != null && touchButton != null) {
            final int charHeight = label.getBitmapFont().getCharHeight();
            touchButton.setHeight(height);
            label.setY(getAbsoluteScaleY() * (height - charHeight) / 2);
        }

        if (icon != null) {
            icon.setY((height - icon.getHeight() * icon.getAbsoluteScaleY()) / 2);
        }
    }

    @Override
    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    public boolean isEnabled() {
        return touchButton.isEnabled();
    }

    public void setEnabled(boolean value) {
        touchButton.setEnabled(value);

        label.setColor(value ? getColor() : DISABLED_COLOR);

        if (icon != null) {
            //icon.setColor(value ? Color.WHITE : Color.GRAY);
        }
    }

    public Sprite getIcon() {
        return icon;
    }

    public void setIcon(Sprite icon) {
        if (this.icon != null && this.icon.getParent() == this) {
            remove(icon);
        }

        this.icon = icon;

        if (icon != null) {
            if (label.getParent() == this) {
                remove(label);
            }
            add(icon);
        } else {
            add(label);
        }

        setSize(getWidth(), getHeight());
    }


}
