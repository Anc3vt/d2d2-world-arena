/*
 *   D2D2 World Arena Desktop
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
package ru.ancevt.d2d2world.desktop;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
    public static @NotNull Map<String, String> parse(String html) {
        return Arrays.stream(html.split("\n"))
                .filter(s -> s.startsWith("<a href"))
                .map(s -> s.substring(s.indexOf('-') + 1, s.indexOf('.'))
                        + ' ' +
                        s.substring(s.indexOf('"') + 1, s.lastIndexOf('"'))
                )
                .collect(Collectors.toUnmodifiableMap(
                        s -> s.substring(0, s.indexOf(' '))
                        ,
                        s -> s.substring(s.indexOf(' ') + 1))
                );
    }

    public static void main(String[] args) {
        var m = ThanksToHtmlParser.parse("""
                <html>
                     * <head><title>Index of /thanksto/</title></head>
                <body>
                <h1>Index of /thanksto/</h1><hr><pre><a href="../">../</a>
                <a href="thanksto-Me.png">thanksto-Me.png</a>                                    20-Feb-2022 11:37               38396
                <a href="thanksto-Qryptojesus.png">thanksto-Qryptojesus.png</a>                           20-Feb-2022 11:37               24558
                <a href="thanksto-WhiteWorldBridger.png">thanksto-WhiteWorldBridger.png</a>                     20-Feb-2022 11:37               38455
                <a href="thanksto-meeekup.png">thanksto-meeekup.png</a>                               20-Feb-2022 11:37               37730
                </pre><hr></body>
                     * </html>
                """);

        System.out.println(m);
    }
}
