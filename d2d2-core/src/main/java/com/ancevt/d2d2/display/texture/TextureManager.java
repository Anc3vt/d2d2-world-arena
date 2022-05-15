
package com.ancevt.d2d2.display.texture;

import com.ancevt.d2d2.display.text.BitmapText;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureManager {

    private final List<TextureAtlas> textureAtlases;
    private final Map<String, Texture> textures;
    private ITextureEngine textureEngine;

    public TextureManager() {
        textures = new HashMap<>();
        textureAtlases = new ArrayList<>();
    }

    public void setTextureEngine(ITextureEngine textureEngine) {
        this.textureEngine = textureEngine;
    }

    public ITextureEngine getTextureEngine() {
        return textureEngine;
    }

    public TextureAtlas loadTextureAtlas(InputStream pngInputStream) {
        final TextureAtlas result = textureEngine.createTextureAtlas(pngInputStream);
        textureAtlases.add(result);
        return result;
    }

    public TextureAtlas loadTextureAtlas(String assetPath) {
        final TextureAtlas result = textureEngine.createTextureAtlas(assetPath);
        textureAtlases.add(result);
        return result;
    }

    public void unloadTextureAtlas(TextureAtlas textureAtlas) {
        textureEngine.unloadTextureAtlas(textureAtlas);
        textureAtlases.remove(textureAtlas);
        textureAtlas.setDisposed(true);
    }

    public void clear() {
        while (!textureAtlases.isEmpty()) {
            unloadTextureAtlas(textureAtlases.get(0));
        }
    }

    public TextureAtlas bitmapTextToTextureAtlas(BitmapText bitmapText) {
        TextureAtlas textureAtlas = textureEngine.bitmapTextToTextureAtlas(bitmapText);
        textureAtlases.add(textureAtlas);
        return textureAtlas;
    }

    public int getTextureAtlasCount() {
        return textureAtlases.size();
    }

    public TextureAtlas getTextureAtlas(int index) {
        return textureAtlases.get(index);
    }

    public void addTexture(String key, Texture texture) {
        textures.put(key, texture);
    }

    public Texture getTexture(String key) {
        Texture result = textures.get(key);
        if (result == null) {
            throw new IllegalArgumentException("No such texture key: " + key);
        }
        return result;
    }

    public final void loadTextureDataInfo(String assetPath) {
        try {
            TextureDataInfoReadHelper.readTextureDataInfoFile(assetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


















