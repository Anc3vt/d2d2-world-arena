
package com.ancevt.d2d2world.client.scene.intro;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.FramedSprite;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.display.texture.TextureUrlLoader;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.TextureUrlLoaderEvent;
import com.ancevt.d2d2world.client.ui.UiText;
import com.ancevt.d2d2world.client.ui.component.Preloader;

public class ThanksTo extends DisplayObjectContainer {

    public static final int IMAGE_WIDTH = 128;
    public static final int IMAGE_HEIGHT = 128;
    private static TextureAtlas glassEffectAtlas;
    private String textureUrl;
    private String name;
    private Texture texture;
    private final long fileSize;
    private final Preloader preloader = new Preloader();

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

        fileSize = 0L;

        UiText uiText = new UiText();
        uiText.setText(name);

        uiText.setXY((IMAGE_WIDTH - uiText.getTextWidth()) / 2, IMAGE_HEIGHT + 10);
        add(uiText);

        startGlassEffect();
    }

    public ThanksTo(String textureUrl, String name, long fileSize) {
        this.textureUrl = textureUrl;
        this.name = name;
        this.fileSize = fileSize;
        UiText uiText = new UiText();
        uiText.setText(name);
        uiText.getTextWidth();
        uiText.setXY((IMAGE_WIDTH - uiText.getTextWidth()) / 2, IMAGE_HEIGHT + 10);
        add(uiText);
    }

    public void load() {
        PlainRect background = new PlainRect(IMAGE_WIDTH, IMAGE_HEIGHT, Color.of(0x111111));
        add(background);

        if (ThanksToCache.contains(name, fileSize)) {
            showImage(ThanksToCache.getTextureFromCache(name));
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
        ThanksToCache.saveToCache(name, e.getBytes());
        showImage(e.getTextureAtlas().createTexture());
    }

    // TODO: don't forget to implement dispatching this event from TextureUrlLoader
    private void textureUrlLoader_textureLoadError(Event event) {
        // TODO: log error
    }

    private void showImage(Texture texture) {
        this.texture = texture;
        Sprite sprite = new Sprite(texture);
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

    public void dispose() {
        if (texture != null) {
            textureManager().unloadTextureAtlas(texture.getTextureAtlas());
        }
    }

    @Override
    public String toString() {
        return "ThanksTo{" +
                "textureUrl='" + textureUrl + '\'' +
                ", name='" + name + '\'' +
                ", texture=" + texture +
                ", fileSize=" + fileSize +
                ", preloader=" + preloader +
                '}';
    }
}
