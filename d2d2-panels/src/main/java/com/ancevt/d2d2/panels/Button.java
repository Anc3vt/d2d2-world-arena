/*
 *   D2D2 Panels
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
package com.ancevt.d2d2.panels;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.TouchEvent;
import com.ancevt.d2d2.touch.TouchButton;

public class Button extends Panel {

    private static final int PADDING = 8;
    private static final String DEFAULT_BUTTON_LABEL_TEXT = "Button";
    private static final float DEFAULT_WIDTH = 120;
    private static final float DEFAULT_HEIGHT = 30;
    private static final Color DISABLED_COLOR = Color.GRAY;

    private final BitmapText label;
    private final TouchButton touchButton;
    private Sprite icon;
    private boolean pressed;

    public Button() {
        this(DEFAULT_BUTTON_LABEL_TEXT);
    }

    public Button(String labelText) {
        label = new BitmapText();
        label.setColor(Color.BLACK);
        label.setText(labelText);

        touchButton = new TouchButton();

        touchButton.addEventListener(TouchEvent.TOUCH_DOWN, e -> {
            Focus.setFocusedComponent(this);

            borderLeft.setColor(BORDER_COLOR_2);
            borderRight.setColor(BORDER_COLOR_1);
            borderTop.setColor(BORDER_COLOR_2);
            borderBottom.setColor(BORDER_COLOR_1);
            label.move(1, 1);
            pressed = true;
        });

        touchButton.addEventListener(TouchEvent.TOUCH_UP, event -> {
            var e = (TouchEvent) event;

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
            label.setBounds(width - PADDING * 2, charHeight);
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
