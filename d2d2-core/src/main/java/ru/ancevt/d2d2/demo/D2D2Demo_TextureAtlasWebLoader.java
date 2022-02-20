package ru.ancevt.d2d2.demo;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.debug.FpsMeter;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.texture.TextureAtlas;
import ru.ancevt.d2d2.event.TextureUrlLoaderEvent;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;
import ru.ancevt.d2d2.display.texture.TextureUrlLoader;

public class D2D2Demo_TextureAtlasWebLoader {

    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        Root root = D2D2.getStage().getRoot();


        Preloader preloader = new Preloader();
        preloader.setScale(0.5f, 0.5f);

        TextureUrlLoader loader = new TextureUrlLoader("https://d2d2.ancevt.ru/test.png");
        loader.addEventListener(TextureUrlLoaderEvent.TEXTURE_LOAD_START, event -> {
            root.add(preloader, 252 / 2, 167 / 2);
        });
        loader.addEventListener(TextureUrlLoaderEvent.TEXTURE_LOAD_COMPLETE, event -> {
            TextureAtlas atlas = loader.getLastLoadedTextureAtlas();
            Sprite sprite = new Sprite(atlas.createTexture());
            root.add(sprite);
            root.remove(preloader);
            root.add(new FpsMeter());
        });
        loader.load();


        D2D2.loop();
    }

    private static class Preloader extends DisplayObjectContainer {
        static final int FRAMES = 10;
        int timer = FRAMES;

        public Preloader() {
            Sprite sprite = new Sprite(D2D2.getTextureManager().loadTextureAtlas("d2d2-loading.png").createTexture());
            add(sprite, -32, -32);
        }

        @Override
        public void onEachFrame() {
            if (timer-- <= 0) {
                timer = FRAMES;
                rotate(45);
            }
        }
    }
}
