
package com.ancevt.d2d2.asset;

import com.ancevt.d2d2.exception.AssetException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import static com.ancevt.commons.util.Slash.slashSafe;

public class Assets {

    private static final String ASSETS_DIR = "assets/";

    private Assets() {
    }

    public static @NotNull InputStream getAssetAsStream(String assetPath) {
        final ClassLoader classLoader = Assets.class.getClassLoader();

        InputStream result = classLoader.getResourceAsStream(ASSETS_DIR + assetPath);

        if (result == null) throw new AssetException("resource " + assetPath + " not found");

        return result;
    }

    public static @NotNull String readAssetAsString(String assetPath) {
        return readAssetAsString(assetPath, StandardCharsets.UTF_8.name());
    }

    public static @NotNull String readAssetAsString(String assetPath, String charsetName) {
        assetPath = slashSafe(assetPath);

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

    @Contract("_ -> new")
    public static @NotNull BufferedReader getAssetAsBufferedReader(InputStream inputStream) {
        return getAssetAsBufferedReader(inputStream, StandardCharsets.UTF_8.name());
    }

    @Contract("_, _ -> new")
    public static @NotNull BufferedReader getAssetAsBufferedReader(InputStream inputStream, String charsetName) {
        try {
            return new BufferedReader(new InputStreamReader(inputStream, charsetName));
        } catch (UnsupportedCharsetException | IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Contract("_ -> new")
    public static @NotNull BufferedReader getAssetAsBufferedReader(String assetPath) {
        return getAssetAsBufferedReader(getAssetAsStream(assetPath), StandardCharsets.UTF_8.name());
    }

    @Contract("_, _ -> new")
    public static @NotNull BufferedReader getAssetAsBufferedReader(String assetPath, String charsetName) {
        return getAssetAsBufferedReader(getAssetAsStream(assetPath), charsetName);
    }

}
