package ru.ancevt.d2d2world.ui;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Sprite;

public class Preloader extends DisplayObjectContainer {
    public static final String ASSET = "d2d2-world-loading-image.png";

    private static final int FRAMES = 10;
    private int timer = FRAMES;

    private static Sprite sprite;

    public Preloader() {
        add(getSprite(), -32, -32);
    }

    @Override
    public void onEachFrame() {
        if (timer-- <= 0) {
            timer = FRAMES;
            rotate(45);
        }
    }

    private static Sprite getSprite() {
        if(sprite == null) {
            sprite = new Sprite(D2D2.getTextureManager().loadTextureAtlas(ASSET).createTexture());
        }
        return sprite;
    }
}