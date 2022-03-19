package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class ShadowRadial extends DisplayObjectContainer {

    private static Texture texture;
    private final List<Sprite> sprites;

    public ShadowRadial() {
        if (texture == null) {
            texture = textureManager().loadTextureAtlas("shadow-radial.png").createTexture();
        }
        sprites = new ArrayList<>();
        setDarknessValue(5);
    }

    public void setDarknessValue(int value) {
        while (!sprites.isEmpty()) {
            sprites.remove(0).removeFromParent();
        }

        for (int i = 0; i < value; i++) {
            Sprite sprite = new Sprite(texture);
            sprite.setColor(Color.BLACK);
            add(sprite, -sprite.getWidth() / 2, -sprite.getHeight() / 2);
            sprites.add(sprite);
        }
    }

    public int getDarknessValue() {
        return sprites.size();
    }

    public void setColor(Color color) {
        sprites.forEach(s -> s.setColor(color));
    }

    public Color getColor() {
        return sprites.get(0).getColor();
    }

    @Override
    public void onEachFrame() {

    }
}
