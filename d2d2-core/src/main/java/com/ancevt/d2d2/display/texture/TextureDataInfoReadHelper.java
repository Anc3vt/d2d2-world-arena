
package com.ancevt.d2d2.display.texture;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.asset.Assets;

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
