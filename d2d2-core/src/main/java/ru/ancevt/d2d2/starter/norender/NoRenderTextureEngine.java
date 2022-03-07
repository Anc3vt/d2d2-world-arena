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
package ru.ancevt.d2d2.starter.norender;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.asset.Assets;
import ru.ancevt.d2d2.display.text.BitmapText;
import ru.ancevt.d2d2.display.texture.ITextureEngine;
import ru.ancevt.d2d2.display.texture.TextureAtlas;
import ru.ancevt.d2d2.display.texture.TextureCell;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NoRenderTextureEngine implements ITextureEngine {

    private int textureAtlasIdCounter;
    private final Map<Integer, Image> images;

    public NoRenderTextureEngine() {
        images = new HashMap<>();
    }

    @Override
    public boolean bind(TextureAtlas textureAtlas) {
        return false;
    }

    @Override
    public void enable(TextureAtlas textureAtlas) {

    }

    @Override
    public void disable(TextureAtlas textureAtlas) {

    }

    @Override
    public TextureAtlas createTextureAtlas(InputStream pngInputStream) {
        try {
            BufferedImage image = ImageIO.read(pngInputStream);
            textureAtlasIdCounter++;
            images.put(textureAtlasIdCounter, image);
            return new TextureAtlas(
                    textureAtlasIdCounter,
                    image.getWidth(),
                    image.getHeight()
            );
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public TextureAtlas createTextureAtlas(String assetPath) {
        return createTextureAtlas(Assets.getAssetAsStream(assetPath));
    }

    @Override
    public TextureAtlas createTextureAtlas(int width, int height, TextureCell[] cells) {
        textureAtlasIdCounter++;
        images.put(textureAtlasIdCounter, new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        return new TextureAtlas(textureAtlasIdCounter, width, height);
    }

    @Override
    public void unloadTextureAtlas(@NotNull TextureAtlas textureAtlas) {
        images.remove(textureAtlas.getId());
    }

    @Override
    public TextureAtlas bitmapTextToTextureAtlas(BitmapText bitmapText) {
        int width = (int) bitmapText.getBoundWidth();
        int height = (int) bitmapText.getBoundHeight();
        textureAtlasIdCounter++;
        TextureAtlas textureAtlas = new TextureAtlas(textureAtlasIdCounter, width, height);
        D2D2.getTextureManager().addTexture("_textureAtlas_text_" + textureAtlas.getId(), textureAtlas.createTexture());
        return textureAtlas;
    }
}
