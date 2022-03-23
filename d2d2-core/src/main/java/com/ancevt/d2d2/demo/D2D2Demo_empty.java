package com.ancevt.d2d2.demo;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

public class D2D2Demo_empty {


    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));

        root.add(new FpsMeter());

        root.add(new Sprite("satellite"), 100, 100);

        root.setBackgroundColor(Color.DARK_BLUE);

        D2D2.loop();
    }
}
