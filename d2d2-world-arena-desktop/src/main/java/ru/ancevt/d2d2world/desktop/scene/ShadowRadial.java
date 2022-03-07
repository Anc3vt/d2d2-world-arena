package ru.ancevt.d2d2world.desktop.scene;

import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.texture.Texture;

public class ShadowRadial extends Sprite {

    private static Texture texture;
    private float t = 0.f;

    public ShadowRadial() {
        if(texture == null) {
            texture = textureManager().loadTextureAtlas("shadow-radial.png").createTexture();
        }

        setTexture(texture);
    }

    @Override
    public void onEachFrame() {
        t += 0.001;

        float a = getAlpha();

        a -= t;

        setAlpha(a);

        if(a <= 0.5f) {
            t -= 1f;
        }
    }
}
