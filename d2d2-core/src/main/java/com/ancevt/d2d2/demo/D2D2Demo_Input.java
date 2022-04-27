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
import com.ancevt.d2d2.debug.DebugGrid;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

public class D2D2Demo_Input {


    private static IDisplayObject cursor;

    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, D2D2Demo_Input.class.getName() + "(floating)"));

        Root root = D2D2.getStage().getRoot();

        root.addEventListener(InputEvent.KEY_DOWN, D2D2Demo_Input::keyDown);
        root.addEventListener(InputEvent.KEY_UP, D2D2Demo_Input::keyUp);
        root.addEventListener(InputEvent.MOUSE_DOWN, D2D2Demo_Input::mouseDown);
        root.addEventListener(InputEvent.MOUSE_MOVE, D2D2Demo_Input::mouseMove);
        root.addEventListener(InputEvent.MOUSE_WHEEL, D2D2Demo_Input::mouseWheel);

        DisplayObjectContainer container = new DisplayObjectContainer();
        Sprite sprite = new Sprite("satellite");

        container.add(sprite, -sprite.getWidth() / 2, -sprite.getHeight() / 2);
        root.add(container, Mouse.getX(), Mouse.getY());

        cursor = container;
        cursor.setAlpha(0.25f);

        DebugGrid debugGrid = new DebugGrid();
        //debugGrid.setScale(2f,2f);
        root.add(debugGrid);

        D2D2.getStage().setScaleMode(ScaleMode.EXTENDED);
        D2D2.loop();
    }

    private static void mouseDown(Event event) {
        InputEvent e = (InputEvent) event;
        cursor.setXY(e.getX(), e.getY());

        System.out.println("Mouse down " + ((InputEvent) event).getX());
    }

    private static void mouseWheel(Event event) {
        InputEvent e = (InputEvent) event;

        if(e.getDelta() > 0) {
            cursor.toScale(1.1f, 1.1f);
        } else {
            cursor.toScale(0.9f, 0.9f);
        }

        System.out.println("wheel: " + e.getDelta());
    }

    private static void mouseMove(Event event) {
        InputEvent e = (InputEvent) event;

        if(e.isDrag()) {
            cursor.setXY(e.getX(), e.getY());
            System.out.println("move: " + e.getX() + ", " + e.getY() + " " + e.isDrag());
        }
    }

    private static float nextHalf(float v) {
        return (float) (Math.ceil(v * 16) / 16);
    }


    private static void keyDown(Event event) {
        InputEvent e = (InputEvent) event;

        if(e.getKeyCode() == 259) {
            System.out.println("Removed");
            e.getSource().removeAllEventListeners(InputEvent.KEY_DOWN);
        }

        System.out.println(e);
    }

    private static void keyUp(Event event) {
        InputEvent e = (InputEvent) event;

        if(e.getKeyCode() == KeyCode.UP) {
            System.out.println("up");
        }
    }
}
