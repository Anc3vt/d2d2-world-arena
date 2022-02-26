package ru.ancevt.d2d2world.ui;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;

public class Preloader extends DisplayObjectContainer {

    public static final String ASSET = "d2d2-world-common-tileset.png";

    private static final int FRAMES = 10;
    private int timer = FRAMES;

    private static Texture texture;

    public Preloader() {
        add(new Sprite(getOrLoadTexture()), -32, -32);
    }

    @Override
    public void onEachFrame() {
        if (timer-- <= 0) {
            timer = FRAMES;
            rotate(45);
        }
    }

    private static Texture getOrLoadTexture() {
        if (texture == null) {
            texture = D2D2.getTextureManager().loadTextureAtlas(ASSET).createTexture(32, 0, 64, 64);
        }
        return texture;
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        root.add(new Preloader(), 32, 32);
        D2D2.loop();
    }
}