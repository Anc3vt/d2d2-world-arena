/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2.demo;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.event.TextureUrlLoaderEvent;
import com.ancevt.d2d2.display.texture.TextureUrlLoader;

public class D2D2Demo_TextureAtlasWebLoader {

    public static void main(String[] args) {
        D2D2.init(new LWJGLBackend(800, 600, "(floating)"));
        Root root = D2D2.getStage().getRoot();


        Preloader preloader = new Preloader();
        preloader.setScale(0.5f, 0.5f);

        TextureUrlLoader loader = new TextureUrlLoader("https://d2d2.ancevt.com/test.png");
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
