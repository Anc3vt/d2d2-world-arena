package com.ancevt.d2d2.demo;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.MouseButton;
import com.ancevt.d2d2.sound.SoundImpl;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

public class D2D2Demo_Sound {
    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));

        SoundImpl sound = new SoundImpl("sound/tap.ogg");


        root.addEventListener(InputEvent.MOUSE_DOWN, event -> {
            var e = (InputEvent) event;



            switch (e.getMouseButton()) {
                case MouseButton.LEFT -> {
                    sound.play();
                }
                case MouseButton.RIGHT -> {
                    sound.stop();
                    System.out.println(2);
                }
                case MouseButton.MIDDLE -> {
                    System.out.println(3);
                }
            }

        });


        D2D2.loop();
    }
}
