/*
 *   D2D2 World Arena Desktop
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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
package ru.ancevt.d2d2world.desktop.scene.intro;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.FramedSprite;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2.display.texture.TextureAtlas;
import ru.ancevt.d2d2.display.texture.TextureUrlLoader;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.TextureUrlLoaderEvent;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;
import ru.ancevt.d2d2world.desktop.ui.UiText;
import ru.ancevt.d2d2world.ui.Preloader;

public class ThanksTo extends DisplayObjectContainer {

    public static final int IMAGE_WIDTH = 128;
    public static final int IMAGE_HEIGHT = 128;
    private static TextureAtlas glassEffectAtlas;
    private String textureUrl;
    private String name;
    private Preloader preloader = new Preloader();

    /**
     * Legacy constructor
     *
     * @param texture
     * @param name
     * @deprecated
     */
    @Deprecated
    public ThanksTo(Texture texture, String name) {
        Sprite sprite = new Sprite(texture);
        add(sprite);

        UiText uiText = new UiText();
        uiText.setText(name);

        uiText.setXY((IMAGE_WIDTH - uiText.getTextWidth()) / 2, IMAGE_HEIGHT + 10);
        add(uiText);

        startGlassEffect();
    }

    public ThanksTo(String textureUrl, String name) {
        this.textureUrl = textureUrl;
        this.name = name;
        UiText uiText = new UiText();
        uiText.setText(name);
        uiText.getTextWidth();
        uiText.setXY((IMAGE_WIDTH - uiText.getTextWidth()) / 2, IMAGE_HEIGHT + 10);
        add(uiText);
    }

    public void load() {
        PlainRect background = new PlainRect(IMAGE_WIDTH, IMAGE_HEIGHT, Color.of(0x111111));
        add(background);

        if (ThanksToCache.contains(name)) {
            showImage(ThanksToCache.getImage(name));
        } else {
            TextureUrlLoader textureUrlLoader = new TextureUrlLoader(textureUrl);
            textureUrlLoader.addEventListener(TextureUrlLoaderEvent.TEXTURE_LOAD_START, this::textureUrlLoader_textureLoadStart);
            textureUrlLoader.addEventListener(TextureUrlLoaderEvent.TEXTURE_LOAD_COMPLETE, this::textureUrlLoader_textureLoadComplete);
            textureUrlLoader.addEventListener(TextureUrlLoaderEvent.TEXTURE_LOAD_ERROR, this::textureUrlLoader_textureLoadError);
            textureUrlLoader.load();
        }
    }

    private void textureUrlLoader_textureLoadStart(Event event) {
        add(preloader, IMAGE_WIDTH / 2f, IMAGE_HEIGHT / 2f);
    }

    private void textureUrlLoader_textureLoadComplete(Event event) {
        preloader.removeFromParent();
        var e = (TextureUrlLoaderEvent) event;
        showImage(e.getTextureAtlas().createTexture());
    }

    // TODO: don't forget to implement dispatching this event from TextureUrlLoader
    private void textureUrlLoader_textureLoadError(Event event) {
        // TODO: log error
    }

    private void showImage(Texture texture) {
        final Sprite sprite = new Sprite(texture);
        add(sprite);
        startGlassEffect();
    }

    private void startGlassEffect() {
        if (glassEffectAtlas == null) {
            glassEffectAtlas = D2D2.getTextureManager().loadTextureAtlas("thanksto/glare.png");
        }

        Texture[] textures = new Texture[21];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = glassEffectAtlas.createTexture(0, i * 16, 128, 128);
        }

        FramedSprite framedSprite = new FramedSprite(textures) {

            int time = -50;

            @Override
            public void onEachFrame() {
                if (++time % 400 == 0) {
                    setFrame(0);
                    play();
                }
            }
        };
        framedSprite.setSlowing(1);

        add(framedSprite);
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating)"));

        ThanksTo tt = new ThanksTo("https://d2d2.ancevt.ru/thanksto/thanksto-Qryptojesus.png", "Qryptojesus");
        root.add(tt);
        tt.load();

        D2D2.loop();
    }
}
