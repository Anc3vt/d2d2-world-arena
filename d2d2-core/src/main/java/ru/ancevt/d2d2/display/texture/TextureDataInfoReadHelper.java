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
package ru.ancevt.d2d2.display.texture;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.asset.Assets;

import java.io.BufferedReader;
import java.io.IOException;

public class TextureDataInfoReadHelper {

    private TextureDataInfoReadHelper() {
    }

    private static TextureAtlas currentTextureAtlas;

    public static void readTextureDataInfoFile(String assetPath) throws IOException {
        final BufferedReader bufferedReader = Assets.getAssetAsBufferedReader(assetPath);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            parseLine(line);
        }
    }

    private static void parseLine(String line) {
        line = line.trim();
        line = line.replaceAll("\\s+", " ");

        if (line.length() == 0) return;

        char firstChar = line.charAt(0);
        if (firstChar == ':') {
            String tileSetName = line.substring(1);
            currentTextureAtlas = D2D2.getTextureManager().loadTextureAtlas(tileSetName);
            return;
        }

        String[] splitted = line.split(" ");

        String textureKey = splitted[0];
        int x = Integer.parseInt(splitted[1]);
        int y = Integer.parseInt(splitted[2]);
        int w = Integer.parseInt(splitted[3]);
        int h = Integer.parseInt(splitted[4]);

        Texture texture = currentTextureAtlas.createTexture(x, y, w, h);
        D2D2.getTextureManager().addTexture(textureKey, texture);
    }

}
