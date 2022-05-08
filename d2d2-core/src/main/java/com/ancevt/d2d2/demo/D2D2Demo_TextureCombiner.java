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
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.ScaleMode;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.display.texture.TextureCombiner;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.input.KeyCode;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;

public class D2D2Demo_TextureCombiner {
    private static Root root;
    private static Sprite sprite;
    private static Sprite sprite2;

    public static void main(String[] args) {
        D2D2.init(new LWJGLBackend(800, 600, "D2D2Demo TextureCombiner (floating)"));

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

            c.append(textureSat, x, y, Color.WHITE, scale, scale, scale, i * 10, 2, 2);
        }

        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * c.getWidth());
            int y = (int) (Math.random() * c.getHeight());
            float scale = (float) (Math.random());

            c.append(textureTest, x, y, Color.WHITE, scale, scale, scale, i * 10, 2, 2);
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
        } else if (inputEvent.getKeyChar() == 'S') {
            root.addEventListener(Event.EACH_FRAME, e -> {
                for (int i = 0; i < 1; i++) {
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
















