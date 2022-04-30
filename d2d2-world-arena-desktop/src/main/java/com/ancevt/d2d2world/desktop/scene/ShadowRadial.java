package com.ancevt.d2d2world.desktop.scene;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.backend.lwjgl.LWJGLStarter;

import java.util.ArrayList;
import java.util.List;

public class ShadowRadial extends DisplayObjectContainer {

    private static Texture texture;
    private final List<Sprite> sprites;
    private Color color = Color.WHITE;

    public ShadowRadial() {
        if (texture == null) {
            texture = textureManager().loadTextureAtlas("shadow-radial.png").createTexture();
        }
        sprites = new ArrayList<>();
        setValue(1);
    }

    public void setValue(int value) {
        while (!sprites.isEmpty()) {
            sprites.remove(0).removeFromParent();
        }

        for (int i = 0; i < value; i++) {
            Sprite sprite = new Sprite(texture);
            sprite.setColor(color);
            float sw = D2D2.getStage().getStageWidth();
            float sh = D2D2.getStage().getStageHeight();

            float scaleX = sw / sprite.getWidth();
            float scaleY = sh / sprite.getHeight();
            sprite.setScale(scaleX, scaleY);
            add(sprite, (-sprite.getWidth() * scaleX) / 2, (-sprite.getHeight() * scaleY) / 2);

            sprites.add(sprite);
        }
    }

    public int getValue() {
        return sprites.size();
    }

    public void setColor(Color color) {
        this.color = color;
        sprites.forEach(s -> s.setColor(color));
    }

    public Color getColor() {
        return sprites.get(0).getColor();
    }

    @Override
    public void onEachFrame() {

    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));
        root.setBackgroundColor(Color.WHITE);

        ShadowRadial shadowRadial = new ShadowRadial();
        shadowRadial.setColor(Color.BLACK);


        root.add(shadowRadial);
        D2D2.loop();
    }
}
