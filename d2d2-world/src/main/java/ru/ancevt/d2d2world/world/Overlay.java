/*
 *   D2D2 World
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
package ru.ancevt.d2d2world.world;

import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.event.Event;

public class Overlay extends PlainRect {

    private static final Color COLOR = Color.BLACK;
    public static final int STATE_IN = 0;
    public static final int STATE_BLACK = 1;
    public static final int STATE_OUT = 2;
    public static final int STATE_DONE = 3;

    private static final float ALPHA_SPEED = 0.025f;

    private int state;
    private float alpha;

    public Overlay(float width, float height) {
        setColor(COLOR);
        setSize(width, height);
        setAlpha(0f);
    }

    public void startIn() {
        if(state == STATE_BLACK) return;
        state = STATE_IN;
        removeEventListeners(getClass());
        addEventListener(getClass(), Event.EACH_FRAME, this::eachFrame);
    }

    public void startOut() {
        if(state == STATE_DONE) return;
        state = STATE_OUT;
        removeEventListeners(getClass());
        addEventListener(getClass(), Event.EACH_FRAME, this::eachFrame);
    }

    private void eachFrame(Event event) {
        switch (getState()) {
            case STATE_IN -> {
                alpha += ALPHA_SPEED;
                setAlpha(Math.min(alpha, 1.0f));
                if (alpha >= 1.2f) {
                    setState(STATE_BLACK);
                    removeEventListeners(getClass());
                }
            }
            case STATE_OUT -> {
                alpha -= ALPHA_SPEED;
                setAlpha(Math.max(alpha, 0.0f));
                if (alpha <= -0.2f) {
                    setState(STATE_DONE);
                    removeEventListeners(getClass());
                }
            }
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        onStateChanged(state);
    }

    public void onStateChanged(int state) {
        dispatchEvent(new Event(Event.CHANGE, this));
    }
}
