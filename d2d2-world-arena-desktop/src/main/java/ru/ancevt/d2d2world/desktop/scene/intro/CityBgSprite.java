package ru.ancevt.d2d2world.desktop.scene.intro;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.texture.Texture;

public class CityBgSprite extends Sprite {

    private static final float SPEED = 1;
    private static Texture texture;

    public static Texture createOrGetTexture() {
        if (texture == null) {
            texture = D2D2.getTextureManager().loadTextureAtlas("city-bg.png").createTexture();
        }
        return texture;
    }

    public CityBgSprite() {
        super(createOrGetTexture());
        setColor(Color.BLACK);
        setRepeatX(3);
        setScale(2f,2f);
    }

    @Override
    public void onEachFrame() {
        moveX(-SPEED);
        if(getX() < -getTexture().width() * getScaleX()) {
            setX(0);
        }
    }
}
