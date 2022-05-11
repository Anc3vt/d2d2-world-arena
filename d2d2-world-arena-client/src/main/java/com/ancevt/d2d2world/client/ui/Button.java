/*
 *   D2D2 World Arena Client
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
package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.TouchButtonEvent;
import com.ancevt.d2d2.touch.TouchButton;
import com.ancevt.d2d2world.client.D2D2WorldArenaDesktopAssets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import static com.ancevt.commons.unix.UnixDisplay.debug;

public class Button extends DisplayObjectContainer {

    private static final float DEFAULT_WIDTH = 80;
    private static final String DEFAULT_TEXT = "Button";

    private final Sprite leftPart;
    private final Sprite rightPart;
    private final Sprite middlePart;
    private final UiText uiText;
    private final TouchButton touchButton;
    private float width;

    public Button(String text) {
        leftPart = new Sprite(D2D2WorldArenaDesktopAssets.getButtonLeftPartTexture());
        rightPart = new Sprite(D2D2WorldArenaDesktopAssets.getButtonRightPartTexture());
        middlePart = new Sprite(D2D2WorldArenaDesktopAssets.getButtonMiddlePartTexture());

        uiText = new UiText();

        touchButton = new TouchButton(true);
        touchButton.addEventListener(TouchButtonEvent.TOUCH_DOWN, this::touchButton_touchDown);
        add(touchButton);

        add(leftPart);
        add(middlePart);
        add(rightPart);

        add(uiText);

        setWidth(DEFAULT_WIDTH);
        setText(text);
    }

    private void touchButton_touchDown(Event<TouchButton> event) {
        dispatchEvent(ButtonEvent.builder().type(ButtonEvent.BUTTON_PRESSED).build());
    }

    public void setEnabled(boolean enabled) {
        touchButton.setEnabled(enabled);
        uiText.setColor(enabled ? Color.WHITE : Color.GRAY);
    }

    public boolean isEnabled() {
        return touchButton.isEnabled();
    }

    public void setText(String text) {
        uiText.setText(text);
        fixTextXY();
    }

    public String getText() {
        return uiText.getText();
    }

    public void setWidth(float width) {
        this.width = width;
        touchButton.setSize(width, leftPart.getTexture().height());

        middlePart.setX(leftPart.getTexture().width());
        middlePart.setScaleX(width - leftPart.getTexture().width() - rightPart.getTexture().width());
        rightPart.setX(leftPart.getTexture().width() + middlePart.getScaleX());

        fixTextXY();
    }

    @Override
    public float getWidth() {
        return width;
    }

    private void fixTextXY() {
        float w = uiText.getTextWidth() - 5;
        uiText.setX((getWidth() - w) / 2);
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class ButtonEvent extends Event<Button> {
        public static final String BUTTON_PRESSED = "buttonPressed";
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2WorldArenaDesktopAssets.load();
        Button button = new Button("Test");
        button.addEventListener(ButtonEvent.BUTTON_PRESSED, event -> {
            debug("Button:104: <A>TEST");
        });
        root.add(button, 100, 100);
        D2D2.loop();
    }
}





























