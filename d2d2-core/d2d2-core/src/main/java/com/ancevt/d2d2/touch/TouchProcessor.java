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
package com.ancevt.d2d2.touch;

import com.ancevt.d2d2.event.EventPool;
import com.ancevt.d2d2.event.TouchButtonEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TouchProcessor {

    public static final TouchProcessor instance = new TouchProcessor();

    private final List<TouchButton> touchableComponents;

    private TouchProcessor() {
        touchableComponents = new CopyOnWriteArrayList<>();
    }

    public void registerTouchableComponent(final TouchButton touchButton) {
        if (!touchableComponents.contains(touchButton))
            touchableComponents.add(touchButton);
    }

    public final void unregisterTouchableComponent(final TouchButton touchButton) {
        touchableComponents.remove(touchButton);
    }

    public final void clear() {
        while (!touchableComponents.isEmpty()) {
            touchableComponents.remove(0);
        }
    }

    public final void screenTouch(final int x, final int y, final int pointer, final boolean down) {

        final Touch t = Touch.touch(pointer);
        if (t == null) return;

        t.setUp(x, y, down);

        if (!down && t.getTouchButton() != null) {

            final TouchButton touchButton = t.getTouchButton();

            if (touchButton.isOnScreen()) {

                final float tcX = touchButton.getAbsoluteX();
                final float tcY = touchButton.getAbsoluteY();
                final float tcW = touchButton.getTouchArea().getWidth() * touchButton.getAbsoluteScaleX();
                final float tcH = touchButton.getTouchArea().getHeight() * touchButton.getAbsoluteScaleY();

                final boolean onArea = x >= tcX && x <= tcX + tcW && y >= tcY && y <= tcY + tcH;

                touchButton.dispatchEvent(
                        EventPool.createTouchEvent(TouchButtonEvent.TOUCH_UP,
                                (int) (x - tcX), (int) (y - tcY), onArea
                        )
                );
                touchButton.setDragging(false);
                t.setTouchButton(null);
                return;
            }
        }

        for (final TouchButton touchButton : touchableComponents) {
            final float tcX = touchButton.getAbsoluteX();
            final float tcY = touchButton.getAbsoluteY();
            final float tcW = touchButton.getTouchArea().getWidth() * touchButton.getAbsoluteScaleX();
            final float tcH = touchButton.getTouchArea().getHeight() * touchButton.getAbsoluteScaleY();

            if (touchButton.isOnScreen() && x >= tcX && x <= tcX + tcW && y >= tcY && y <= tcY + tcH && down) {
                t.setTouchButton(touchButton);
                t.getTouchButton().setDragging(true);
                touchButton.dispatchEvent(
                        EventPool.createTouchEvent(TouchButtonEvent.TOUCH_DOWN,
                                (int) (x - tcX), (int) (y - tcY), true
                        )
                );
            }
        }
    }

    public final void screenDrag(int i, final int x, final int y) {

        for (final TouchButton touchButton : touchableComponents) {
            final float tcX = touchButton.getAbsoluteX();
            final float tcY = touchButton.getAbsoluteY();
            final float tcW = touchButton.getTouchArea().getWidth() * touchButton.getAbsoluteScaleX();
            final float tcH = touchButton.getTouchArea().getHeight() * touchButton.getAbsoluteScaleY();

            final boolean onArea = x >= tcX && x <= tcX + tcW && y >= tcY && y <= tcY + tcH;

            if (touchButton.isOnScreen() && touchButton.isDragging()) {
                touchButton.dispatchEvent(
                        EventPool.createTouchEvent(TouchButtonEvent.TOUCH_DRAG,
                                (int) (x - tcX), (int) (y - tcY), onArea
                        )
                );
            }
            if (touchButton.isOnScreen() && onArea) {
                touchButton.dispatchEvent(
                        EventPool.createTouchEvent(TouchButtonEvent.TOUCH_HOVER,
                                (int) (x - tcX), (int) (y - tcY), true
                        )
                );
            }
        }
    }

    public final String getActiveList() {
        final StringBuilder sb = new StringBuilder();

        for (TouchButton current : touchableComponents) {
            final String s = current.toString();
            sb.append(s).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("TouchProcessor[%d buttons]", touchableComponents.size());
    }
}

class Touch {

    private static final int MAX_TOUCHES = 4;

    private static final Touch[] touches = new Touch[MAX_TOUCHES];

    public static Touch touch(final int pointer) {
        for (Touch value : touches) {
            if (pointer >= MAX_TOUCHES) return null;
            if (value != null && value.getPointer() == pointer) return value;
        }

        final Touch touch = new Touch(pointer);
        touches[pointer] = touch;

        return touch;
    }

    private final int pointer;
    private int x;
    private int y;
    private boolean down;
    private TouchButton component;

    private Touch(final int pointer) {
        this.pointer = pointer;
    }

    public final int getPointer() {
        return pointer;
    }

    public final void setUp(final int x, final int y, boolean down) {
        setLocation(x, y);
        setDown(down);
    }

    public final void setLocation(final int x, final int y) {
        setX(x);
        setY(y);
    }

    public final int getY() {
        return y;
    }

    public final void setY(int y) {
        this.y = y;
    }

    public final int getX() {
        return x;
    }

    public final void setX(int x) {
        this.x = x;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public final TouchButton getTouchButton() {
        return component;
    }

    public final void setTouchButton(final TouchButton component) {
        this.component = component;
    }
}




















