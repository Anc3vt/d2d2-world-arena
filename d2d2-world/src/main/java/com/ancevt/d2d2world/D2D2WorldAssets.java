package com.ancevt.d2d2world;

import com.ancevt.d2d2.display.texture.Texture;

import static com.ancevt.d2d2.D2D2.getTextureManager;

public class D2D2WorldAssets {

    public static void load() {
        getTextureManager().loadTextureDataInfo("d2d2-world.inf");
    }

    public static Texture getPickupBubbleTexture32() {
        return getTextureManager().getTexture("d2d2-world-pickup-bubble-32px");
    }

    public static Texture getPickupBubbleTexture16() {
        return getTextureManager().getTexture("d2d2-world-pickup-bubble-16px");
    }

    public static Texture getRopeTexture() {
        return getTextureManager().getTexture("d2d2-world-rope");
    }

    public static Texture getWaterBubbleTexture() {
        return getTextureManager().getTexture("d2d2-world-water-bubble");
    }
}
