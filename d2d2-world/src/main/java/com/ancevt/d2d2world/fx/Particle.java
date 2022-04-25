package com.ancevt.d2d2world.fx;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.*;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;
import com.ancevt.d2d2world.D2D2World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static java.lang.Math.random;

public class Particle extends DisplayObjectContainer {

    private FramedSprite framed;
    private ISprite sprite;

    private Particle() {
        Texture texture = textureManager().getTexture("d2d2-world-particles-default0");

        Texture[] textures = new Texture[]{
                texture.getSubtexture(0, 0, 16, 16),
                texture.getSubtexture(16, 0, 16, 16),
                texture.getSubtexture(32, 0, 16, 16),
        };

        framed = new FramedSprite(textures);
        framed.setLoop(true);
        framed.setXY(-8, -8);
        framed.setFrame((int) (Math.random() * 3) + 1);
        framed.play();
        add(framed);
    }

    private Particle(Texture texture) {
        sprite = new Sprite(texture);
        add(sprite, -sprite.getWidth() / 2, -sprite.getHeight() / 2);
    }

    public void setColor(Color color) {
        if(framed != null) framed.setColor(color);
        if(sprite != null) sprite.setColor(color);
    }

    public Color getColor() {
        if(framed != null) return framed.getColor();
        if(sprite != null) return sprite.getColor();

        throw new IllegalStateException("framed == null && sprite == null");
    }

    public static @NotNull IDisplayObject miniExplosionDestroyable(Texture[] textures, int amount, Color color, float factor) {
        DisplayObjectContainer doc = new DisplayObjectContainer() {

            int time = 250;

            @Override
            public void onEachFrame() {
                super.onEachFrame();
                time--;
                if (time <= 0) {
                    removeFromParent();
                }
            }
        };

        for (int i = 0; i < amount; i++) {

            Texture texture = textures[new Random().nextInt(textures.length)];

            Particle p = new Particle(texture) {

                float b = (float) (factor * random() - factor / 2);
                float t = (float) (factor * random() - factor / 2) - 0.5f;

                @Override
                public void onEachFrame() {
                    super.onEachFrame();
                    move(b, t);
                    rotate(b);
                    t += 0.1f;
                }
            };

            p.setColor(color);
            doc.add(p);
        }

        return doc;
    }


    public static @NotNull IDisplayObject miniExplosion(int amount, Color color, float factor) {
        DisplayObjectContainer doc = new DisplayObjectContainer() {

            int time = 250;

            @Override
            public void onEachFrame() {
                super.onEachFrame();
                time--;
                if (time <= 0) {
                    removeFromParent();
                }
            }
        };

        for (int i = 0; i < amount; i++) {

            Particle p = new Particle() {

                {
                    setScale((float) (random() + 1f), (float) (random() + 1f));
                }

                float b = (float) (factor * random() - factor / 2);
                float t = (float) (factor * random() - factor / 2);

                @Override
                public void onEachFrame() {
                    super.onEachFrame();
                    move(b, t);
                    toAlpha(0.975f);
                    toScale(1.01f, 1.01f);
                    rotate(45);
                    t += 0.1f;
                }
            };

            p.setColor(color);
            doc.add(p);
        }

        return doc;
    }

    public static @NotNull IDisplayObject water(int amount, Color color, float factor) {
        DisplayObjectContainer doc = new DisplayObjectContainer() {

            int time = 250;

            @Override
            public void onEachFrame() {
                super.onEachFrame();
                time--;
                if (time <= 0) {
                    removeFromParent();
                }
            }
        };

        for (int i = 0; i < amount; i++) {

            Particle p = new Particle() {

                {
                    setScale((float) (random() + 1f), (float) (random() + 1f));
                }

                float b = (float) (factor * random() - factor / 2);
                float t = -(float) (factor * random() - factor / 2);

                @Override
                public void onEachFrame() {
                    super.onEachFrame();
                    move(b, t);
                    toAlpha(0.9f);
                    toScale(1.001f, 1.001f);
                    rotate(45);
                    t += 0.1f;
                    if (getY() > 0) {
                        setY(0);
                        t = 0;
                    }
                }
            };

            p.setColor(color);
            doc.add(p);
        }

        return doc;
    }


    public static @NotNull IDisplayObject bloodExplosion(int amount, Color color) {
        DisplayObjectContainer doc = new DisplayObjectContainer() {

            int time = 250;

            @Override
            public void onEachFrame() {
                super.onEachFrame();
                time--;
                if (time <= 0) {
                    removeFromParent();
                }
            }
        };

        doc.setScale(2f, 2f);

        for (int i = -amount / 2; i < amount / 2; i++) {

            final float sb = i;

            Particle p = new Particle() {

                {
                    setScale((float) (random() + 1f), (float) (random() + 1f));
                }

                float b = (float) (sb / 50 * random());
                float t = (float) (-5 * random());

                @Override
                public void onEachFrame() {
                    super.onEachFrame();
                    move(b, t);
                    toAlpha(0.975f);
                    toScale(1.01f, 1.01f);
                    rotate(45);
                    t += 0.1f;
                }
            };

            p.setColor(color);
            doc.add(p);
        }

        return doc;
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));

        D2D2World.init(true, false);


        root.add(Particle.bloodExplosion(500, Color.of(0x220000)), 300, 300);

        D2D2.loop();
    }
}














