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
package ru.ancevt.d2d2.display.text;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.asset.Assets;
import ru.ancevt.d2d2.display.texture.TextureAtlas;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BitmapFont {

    private static final int MAX_CHARS = 65536;

    private static BitmapFont defaultBitmapFont;
    private static final String BITMAP_FONTS_DIR = "bitmapfonts/";

    private final BitmapCharInfo[] charInfos;
    private final TextureAtlas textureAtlas;


    private BitmapFont(TextureAtlas textureAtlas, BitmapCharInfo[] charInfos) {
        this.textureAtlas = textureAtlas;
        this.charInfos = charInfos;
    }

    public final boolean isCharSupported(char c) {
        return getCharInfo(c) != null;
    }

    public final BitmapCharInfo getCharInfo(char c) {
        return charInfos[c];
    }

    public final int getCharHeight() {
        return charInfos['0'].height();
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    @Override
    public String toString() {
        return "BitmapFont{" +
                ", textureAtlas=" + textureAtlas +
                '}';
    }

    // ------------------- Loading stuff: ---------------------------------------------

    private static final Map<String, BitmapFont> cache = new HashMap<>();

    public static void setDefaultBitmapFont(final BitmapFont bitmapFont) {
        defaultBitmapFont = bitmapFont;
    }

    public static BitmapFont getDefaultBitmapFont() {
        return defaultBitmapFont;
    }

    public static void loadDefaultBitmapFont(String assetPath) {
        BitmapFont.setDefaultBitmapFont(BitmapFont.loadBitmapFont(assetPath));
    }

    public static BitmapFont loadBitmapFont(String bmfAssetPath) {
        BitmapFont fromCache = cache.get(bmfAssetPath);

        if(fromCache != null) {
            return fromCache;
        }

        try (DataInputStream dataInputStream = new DataInputStream(Assets.getAssetAsStream(BITMAP_FONTS_DIR + bmfAssetPath))) {

            BitmapCharInfo[] charInfos = new BitmapCharInfo[MAX_CHARS];

            int metaSize = dataInputStream.readUnsignedShort();

            while (metaSize > 0) {
                char character = dataInputStream.readChar();
                int x = dataInputStream.readUnsignedShort();
                int y = dataInputStream.readUnsignedShort();
                int width = dataInputStream.readUnsignedShort();
                int height = dataInputStream.readUnsignedShort();

                BitmapCharInfo bitmapCharInfo = new BitmapCharInfo(character, x, y, width, height);

                charInfos[character] = bitmapCharInfo;

                metaSize -= Character.BYTES;
                metaSize -= Short.BYTES;
                metaSize -= Short.BYTES;
                metaSize -= Short.BYTES;
                metaSize -= Short.BYTES;

                //Trace.trace("c,x,y,w,h", character, x, y, width, height);
            }

            // Passing rest of the data to loadTextureAtlas. PNG input stream expected
            BitmapFont result = new BitmapFont(D2D2.getTextureManager().loadTextureAtlas(dataInputStream), charInfos);

            cache.put(bmfAssetPath, result);

            return result;
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}

















