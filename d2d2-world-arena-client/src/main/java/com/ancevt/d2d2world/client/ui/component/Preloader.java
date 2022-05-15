
package com.ancevt.d2d2world.client.ui.component;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.client.D2D2WorldArenaDesktopAssets;

public class Preloader extends DisplayObjectContainer {

    private static final int FRAMES_PER_ROTATE = 10;
    private int timer = FRAMES_PER_ROTATE;

    public Preloader() {
        add(new Sprite(D2D2WorldArenaDesktopAssets.getPreloaderTexture()), -32, -32);
    }

    @Override
    public void onEachFrame() {
        if (timer-- <= 0) {
            timer = FRAMES_PER_ROTATE;
            rotate(45);
        }
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        D2D2.setSmoothMode(true);
        D2D2World.init(false, false);
        root.add(new Preloader(), 100, 100);
        D2D2.loop();
    }
}