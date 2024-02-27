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
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.event.Event;

public class Overlay extends PlainRect {

    private static final Color COLOR = Color.BLACK;
    public static final int STATE_IN = 0;
    public static final int STATE_BLACK = 1;
    public static final int STATE_OUT = 2;
    public static final int STATE_DONE = 3;

    private static final float ALPHA_SPEED = 0.1f;

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
        removeEventListener(this, Event.ENTER_FRAME);
        addEventListener(this, Event.ENTER_FRAME, this::eachFrame);
    }

    public void startOut() {
        if(state == STATE_DONE) return;
        state = STATE_OUT;
        removeEventListener(this, Event.ENTER_FRAME);
        addEventListener(this, Event.ENTER_FRAME, this::eachFrame);
    }

    private void eachFrame(Event event) {
        switch (getState()) {
            case STATE_IN -> {
                alpha += ALPHA_SPEED;
                setAlpha(Math.min(alpha, 1.0f));
                if (alpha >= 1.2f) {
                    setState(STATE_BLACK);
                    removeEventListener(this, Event.ENTER_FRAME);
                }
            }
            case STATE_OUT -> {
                alpha -= ALPHA_SPEED;
                setAlpha(Math.max(alpha, 0.0f));
                if (alpha <= -0.2f) {
                    setState(STATE_DONE);
                    removeEventListener(this, Event.ENTER_FRAME);
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
        dispatchEvent(Event.builder()
                .type(Event.CHANGE)
                .build());
    }
}
