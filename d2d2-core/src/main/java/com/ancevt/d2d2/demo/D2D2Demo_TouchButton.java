/*
 *   D2D2 core
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
package com.ancevt.d2d2.demo;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventListener;
import com.ancevt.d2d2.event.TouchButtonEvent;
import com.ancevt.d2d2.backend.lwjgl.LWJGLStarter;
import com.ancevt.d2d2.touch.TouchButton;

import java.util.Objects;

public class D2D2Demo_TouchButton {

    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, D2D2Demo_TouchButton.class.getName() + "(floating)"));

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button(50, 50);
                button.setXY(i * 50, j * 50);
                D2D2.getStage().getRoot().add(button);
            }
        }
        D2D2.getStage().setScaleMode(ScaleMode.EXTENDED);
        D2D2.getStage().getRoot().add(new FpsMeter());

        D2D2.loop();
    }


    private static class Button extends DisplayObjectContainer implements EventListener {
        private final PlainRect plainRect;

        public Button(int w, int h) {
            plainRect = new PlainRect(w, h, Color.DARK_GRAY);
            TouchButton touchButton = new TouchButton(w, h);
            touchButton.setEnabled(true);
            touchButton.addEventListener(TouchButtonEvent.TOUCH_DOWN, this);
            touchButton.addEventListener(TouchButtonEvent.TOUCH_DRAG, this::touchDrag);
            touchButton.addEventListener(TouchButtonEvent.TOUCH_HOVER, this::touchHover);


            add(plainRect);
            add(touchButton);
        }

        private void touchHover(Event event) {
            TouchButtonEvent e = (TouchButtonEvent) event;
            plainRect.setColor(Color.GRAY);
        }

        private void touchDrag(Event event) {
            TouchButtonEvent e = (TouchButtonEvent) event;
            if (e.isOnArea()) {
                plainRect.setColor(Color.DARK_GREEN);
            } else {
                plainRect.setColor(Color.DARK_GRAY);
            }
        }

        @Override
        public void onEvent(Event event) {
            if(Objects.equals(event.getType(), TouchButtonEvent.TOUCH_DOWN)) {
                System.out.println(this);
            }
        }
    }
}
