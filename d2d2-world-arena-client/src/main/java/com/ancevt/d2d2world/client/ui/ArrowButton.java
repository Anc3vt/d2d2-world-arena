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

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.TouchButtonEvent;
import com.ancevt.d2d2.touch.TouchButton;
import com.ancevt.d2d2world.client.D2D2WorldArenaDesktopAssets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

public class ArrowButton extends DisplayObjectContainer {

    private final Sprite sprite;
    private final Sprite shadow;
    private final TouchButton touchButton;
    private int direction;

    public ArrowButton() {
        sprite = new Sprite(D2D2WorldArenaDesktopAssets.getArrowButtonTexture());
        shadow = new Sprite(D2D2WorldArenaDesktopAssets.getArrowButtonTexture());
        shadow.setColor(Color.BLACK);

        add(shadow, 1, 1);
        add(sprite);

        touchButton = new TouchButton((int) sprite.getWidth(), (int) sprite.getHeight(), true);
        add(touchButton);

        touchButton.addEventListener(TouchButtonEvent.TOUCH_DOWN, this::touchButton_touchDown);

        setEnabled(true);
    }

    @Override
    public float getWidth() {
        return sprite.getTexture().width();
    }

    public void setEnabled(boolean enabled) {
        touchButton.setEnabled(enabled);
        sprite.setColor(enabled ? Color.WHITE : Color.GRAY);
    }

    public boolean isEnabled() {
        return touchButton.isEnabled();
    }

    public void setDirection(int direction) {
        this.direction = direction;
        if (direction == -1) {
            sprite.setScaleX(-1f);
            shadow.setScaleX(-1f);
            sprite.setX(sprite.getTexture().width());
        } else {
            sprite.setScaleX(1f);
            shadow.setScaleX(1f);
            sprite.setXY(0f, 0f);
        }

        shadow.setXY(sprite.getX(), sprite.getY());
        shadow.move(1f, 1f);
    }

    public int getDirection() {
        return direction;
    }

    private void touchButton_touchDown(Event<TouchButton> event) {
        dispatchEvent(
                ArrowButtonEvent.builder()
                        .type(ArrowButtonEvent.ARROW_BUTTON_PRESS)
                        .build()
        );
    }

    public void dispose() {
        touchButton.setEnabled(false);
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class ArrowButtonEvent extends Event<ArrowButton> {
        public static final String ARROW_BUTTON_PRESS = "arrowButtonPress";
    }
}
