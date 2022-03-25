/*
 *   D2D2 core
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


















