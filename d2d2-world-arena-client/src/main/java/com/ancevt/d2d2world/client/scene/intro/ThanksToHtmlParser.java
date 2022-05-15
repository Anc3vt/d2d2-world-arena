
package com.ancevt.d2d2world.client.scene.intro;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.parseLong;

public class ThanksToHtmlParser {
    /*
     * <html>
     * <head><title>Index of /thanksto/</title></head>
     * <body>
     * <h1>Index of /thanksto/</h1><hr><pre><a href="../">../</a>
     * <a href="thanksto-Me.png">thanksto-Me.png</a>                                    20-Feb-2022 11:37               38396
     * <a href="thanksto-Qryptojesus.png">thanksto-Qryptojesus.png</a>                           20-Feb-2022 11:37               24558
     * <a href="thanksto-WhiteWorldBridger.png">thanksto-WhiteWorldBridger.png</a>                     20-Feb-2022 11:37               38455
     * <a href="thanksto-meeekup.png">thanksto-meeekup.png</a>                               20-Feb-2022 11:37               37730
     * </pre><hr></body>
     * </html>
     *
     * Qryptojesus thanksto-Qryptojesus.png
     */

    /**
     * Returns map of name and png like "Qryptojesus":"thanksto-Qryptojesus.png"
     *
     * @param html
     * @return Map of _name_:_png_file_name_
     */
    public static @NotNull Map<String, Line> parse(@NotNull String html) {
        Map<String, Line> result = new HashMap<>();

        String[] lines = html.split("\n");
        for (String line : lines) {
            if (!line.startsWith("<a href")) continue;

            String name = line.substring(line.indexOf('-') + 1, line.indexOf('.'));
            String png = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"'));
            String size = line.substring(line.lastIndexOf(' ') + 1).trim();

            try {
                result.put(name, new Line(png, parseLong(size)));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }

        return result;
    }

    public record Line(String pngFileName, long fileSize) {
    }

}
