package ru.ancevt.d2d2.demo;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.debug.FpsMeter;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.ScaleMode;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2.display.texture.TextureAtlas;
import ru.ancevt.d2d2.display.texture.TextureCombiner;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.input.KeyCode;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;

public class D2D2Demo_TextureCombiner {
    private static Root root;
    private static Sprite sprite;
    private static Sprite sprite2;

    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, "D2D2Demo TextureCombiner (floating)"));

        D2D2.getStage().setScaleMode(ScaleMode.REAL);

        root = new Root();
        root.addEventListener(InputEvent.KEY_DOWN, D2D2Demo_TextureCombiner::keyDown);

        start();

        sprite2 = new Sprite(D2D2.getTextureManager().loadTextureAtlas("d2d2-logo.png").createTexture());
        root.add(sprite2, 100, 100);



        root.add(new FpsMeter());
        D2D2.getStage().setRoot(root);
        D2D2.loop();
    }

    private static void start() {
        Texture textureSat = D2D2.getTextureManager().getTexture("satellite");
        Texture textureTest = D2D2.getTextureManager().getTexture("test16x16");

        TextureCombiner c = new TextureCombiner(800, 600);

        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * c.getWidth());
            int y = (int) (Math.random() * c.getHeight());
            float scale = (float) (Math.random());

            c.append(textureSat, x, y, scale, scale, scale, i * 10, 2, 2);
        }

        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * c.getWidth());
            int y = (int) (Math.random() * c.getHeight());
            float scale = (float) (Math.random());

            c.append(textureTest, x, y, scale, scale, scale, i * 10, 2, 2);
        }

        TextureAtlas atlas = c.createTextureAtlas();
        System.out.println("id : " + atlas.getId());

        sprite = new Sprite(atlas.createTexture());
        root.add(sprite);
    }

    private static void keyDown(Event event) {
        InputEvent inputEvent = (InputEvent) event;
        if (inputEvent.getKeyCode() == KeyCode.ENTER) {
            D2D2.getTextureManager().unloadTextureAtlas(sprite.getTexture().getTextureAtlas());
            start();
        } else if (inputEvent.getKeyCode() == KeyCode.UP) {
            D2D2.getTextureManager().unloadTextureAtlas(sprite.getTexture().getTextureAtlas());

            Sprite sprite = new Sprite(D2D2.getTextureManager().loadTextureAtlas("d2d2-logo.png").createTexture());
            root.add(sprite);
        } else if(inputEvent.getKeyChar() == 'S') {
            root.addEventListener(Event.EACH_FRAME, e->{
                for(int i = 0; i < 1; i ++) {
                    D2D2.getTextureManager().unloadTextureAtlas(sprite.getTexture().getTextureAtlas());
                    sprite.removeFromParent();
                    start();
                }
            });
        } else if (inputEvent.getKeyChar() == 'Q') {
            D2D2.getTextureManager().unloadTextureAtlas(sprite2.getTexture().getTextureAtlas());
            D2D2.getTextureManager().loadTextureAtlas("d2d2-logo.png");
        }

    }

}















