
package com.ancevt.d2d2world.client.scene.intro;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;

public class CityBgSprite extends Sprite {

    private static final float SPEED = 1f;
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
        setRepeatX(10);
        setScale(2f,2f);
    }

    @Override
    public void onEachFrame() {
        moveX(-SPEED);
        if(getX() < -getTexture().width() * 2 * getScaleX()) {
            setX(-getTexture().width() * 2);
        }
    }
}
