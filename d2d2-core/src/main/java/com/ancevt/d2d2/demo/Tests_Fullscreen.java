
package com.ancevt.d2d2.demo;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
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

public class Tests_Fullscreen {


    private static IDisplayObject cursor;
    private static Root root;

    public static void main(String[] args) {
        D2D2.init(new LWJGLBackend(800, 600, Tests_Fullscreen.class.getName() + "(floating)"));

        root = D2D2.getStage().getRoot();

        root.addEventListener(InputEvent.KEY_DOWN, Tests_Fullscreen::keyDown);
        root.addEventListener(InputEvent.MOUSE_DOWN, Tests_Fullscreen::mouseDown);
        root.addEventListener(InputEvent.MOUSE_MOVE, Tests_Fullscreen::mouseMove);
        root.addEventListener(InputEvent.MOUSE_WHEEL, Tests_Fullscreen::mouseWheel);

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
        cursor.setXY(e.getX() / root.getAbsoluteScaleX(), e.getY() / root.getAbsoluteScaleY());

        System.out.println("Mouse down " + ((InputEvent) event).getX());
    }

    private static void mouseWheel(Event event) {
        InputEvent e = (InputEvent) event;

        if(e.getDelta() > 0) {
            cursor.toScale(1.1f, 1.1f);
        } else {
            cursor.toScale(0.9f, 0.9f);
        }

        cursor.setXY(e.getX() / root.getAbsoluteScaleX(), e.getY() / root.getAbsoluteScaleY());

        System.out.println("wheel: " + e.getDelta());
    }

    private static void mouseMove(Event event) {
        InputEvent e = (InputEvent) event;

        if(e.isDrag()) {
            cursor.setXY(e.getX() / root.getAbsoluteScaleX(), e.getY() / root.getAbsoluteScaleY());
            System.out.println("move: " + e.getX() + ", " + e.getY() + " " + e.isDrag());
        }
    }

    private static void keyDown(Event event) {
        InputEvent e = (InputEvent) event;
        if(e.getKeyCode() == KeyCode.F) {
            D2D2.setFullscreen(!D2D2.isFullscreen());
        }

        System.out.println(e);
    }
}
