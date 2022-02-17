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
package ru.ancevt.d2d2.asset;

import ru.ancevt.d2d2.exception.AssetException;
import ru.ancevt.d2d2.platform.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public class Assets {

    private Assets() {
    }

    public static InputStream getAssetAsStream(String assetPath) {
        final ClassLoader classLoader = Assets.class.getClassLoader();

        InputStream result = classLoader.getResourceAsStream(Platform.ASSETS_DIRECTORY_PATH + assetPath);

        if (result == null) throw new AssetException("resource " + assetPath + " not found");

        return result;
    }

    public static String readAssetAsString(String assetPath) {
        return readAssetAsString(assetPath, StandardCharsets.UTF_8.name());
    }

    public static String readAssetAsString(String assetPath, String charsetName) {
        final StringBuilder stringBuilder = new StringBuilder();
        try (final BufferedReader bufferedReader = getAssetAsBufferedReader(assetPath, charsetName)) {

            final String endOfLine = String.format("%n");

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(endOfLine);
            }

            return stringBuilder.toString();
        } catch (IOException ex) {
            throw new AssetException(ex);
        }
    }

    public static BufferedReader getAssetAsBufferedReader(InputStream inputStream) {
        return getAssetAsBufferedReader(inputStream, StandardCharsets.UTF_8.name());
    }

    public static BufferedReader getAssetAsBufferedReader(InputStream inputStream, String charsetName) {
        try {
            return new BufferedReader(new InputStreamReader(inputStream, charsetName));
        } catch (UnsupportedCharsetException | IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static BufferedReader getAssetAsBufferedReader(String assetPath) {
        return getAssetAsBufferedReader(getAssetAsStream(assetPath), StandardCharsets.UTF_8.name());
    }

    public static BufferedReader getAssetAsBufferedReader(String assetFilePath, String charsetName) {
        return getAssetAsBufferedReader(getAssetAsStream(assetFilePath), charsetName);
    }

}
