package ru.ancevt.d2d2world.desktop.scene;

import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2.event.Event;

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

        addEventListener(Event.ADD_TO_STAGE, this::this_addToStage);
        setDarknessValue(40);
    }

    private void this_addToStage(Event event) {
        float sw = getStage().getStageWidth();
        float sh = getStage().getStageHeight();
        sprites.forEach(s -> s.setXY(-sw / 2, -sh / 2));
    }

    public void setDarknessValue(int value) {
        while (!sprites.isEmpty()) {
            sprites.remove(0).removeFromParent();
        }

        for (int i = 0; i < value; i++) {
            Sprite sprite = new Sprite(texture);
            sprite.setColor(Color.BLACK);
            add(sprite);
            sprites.add(sprite);
        }

        if (getStage() != null) this_addToStage(null);
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
