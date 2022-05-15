
package com.ancevt.d2d2.display.texture;

import com.ancevt.d2d2.display.text.BitmapText;

import java.io.InputStream;

public interface ITextureEngine {

    boolean bind(TextureAtlas textureAtlas);

    void enable(TextureAtlas textureAtlas);

    void disable(TextureAtlas textureAtlas);

    TextureAtlas createTextureAtlas(InputStream pngInputStream);

    TextureAtlas createTextureAtlas(String assetPath);

    TextureAtlas createTextureAtlas(int width, int height, TextureCell[] cells);

    void unloadTextureAtlas(TextureAtlas textureAtlas);

    TextureAtlas bitmapTextToTextureAtlas(BitmapText bitmapText);

}
