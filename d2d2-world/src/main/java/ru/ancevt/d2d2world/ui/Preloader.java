package ru.ancevt.d2d2world.ui;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;
import ru.ancevt.d2d2world.D2D2World;

public class Preloader extends DisplayObjectContainer {

    private static final int FRAMES_PER_ROTATE = 10;
    private int timer = FRAMES_PER_ROTATE;

    public Preloader() {
        add(new Sprite("d2d2-world-common-tileset-preloader"), -32, -32);
    }

    @Override
    public void onEachFrame() {
        if (timer-- <= 0) {
            timer = FRAMES_PER_ROTATE;
            rotate(45);
        }
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        D2D2World.init();
        root.add(new Preloader(), 32, 32);
        D2D2.loop();
    }
}