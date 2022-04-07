package com.ancevt.d2d2world.fx;

import com.ancevt.d2d2.display.*;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.D2D2World;
import org.jetbrains.annotations.NotNull;

public class SpawnEffect extends DisplayObjectContainer {

    private float speed1 = +8.0f;
    private float speed2 = -4.0f;

    private final Sprite sprite1;
    private final Sprite sprite2;

    private final Texture texture;

    private final int textureX;
    private final int textureY;

    private float y1;
    private float y2;

    private int time;

    public SpawnEffect() {
        texture = D2D2World.getSpawnEffectTexture();

        textureX = texture.x();
        textureY = texture.y();

        sprite1 = new Sprite();
        sprite2 = new Sprite();

        add(sprite1, -texture.width() / 2f, -texture.height() / 2f);
        add(sprite2, -texture.width() / 2f, -texture.height() / 2f);

        setAlpha(0.0f);

        setScaleY(1.5f);
    }

    public void setColor1(Color color) {
        sprite1.setColor(color);
    }

    public Color getColor1() {
        return sprite1.getColor();
    }

    public void setColor2(Color color) {
        sprite2.setColor(color);
    }

    public Color getColor2() {
        return sprite2.getColor();
    }

    @Override
    public void onEachFrame() {
        time++;

        y1 += speed1;
        y2 += speed2;

        sprite1.setTexture(texture.getSubtexture(0, (int) y1, texture.width(), 32));
        sprite2.setTexture(texture.getSubtexture(0, (int) y2, texture.width(), 32));

        if (y1 < 0) {
            y1 = texture.height() - 32;
        } else if (y1 > texture.height() - 32) {
            y1 = 0;
        }

        if (y2 < 0) {
            y2 = texture.height() - 32;
        } else if (y2 > texture.height() - 32) {
            y2 = 0;
        }

        if (time < 10 && getAlpha() < 1f) {
            setAlpha(getAlpha() + 0.05f);
        }

        if (time > 40 && getAlpha() > 0) {
            setAlpha(getAlpha() - 0.05f);
        }

        if (getAlpha() <= 0) removeFromParent();
    }

    public static void doSpawnEffect(@NotNull IDisplayObject target,
                                     @NotNull IDisplayObjectContainer targetParent,
                                     @NotNull Color color) {
        SpawnEffect spawnEffect = new SpawnEffect();
        //spawnEffect.setColor1(color);
        targetParent.add(spawnEffect, target.getX(), target.getY() + 48);
    }

}
