package ru.ancevt.d2d2.event;

import ru.ancevt.d2d2.display.texture.TextureAtlas;

public class TextureUrlLoaderEvent extends Event {

    public static final String TEXTURE_LOAD_COMPLETE = "textureLoadComplete";
    public static final String TEXTURE_LOAD_START = "textureLoadStart";
    public static final String TEXTURE_LOAD_ERROR = "textureLoadError";

    private final TextureAtlas textureAtlas;

    public TextureUrlLoaderEvent(String type, IEventDispatcher source, TextureAtlas textureAtlas) {
        super(type, source);
        this.textureAtlas = textureAtlas;
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }
}
