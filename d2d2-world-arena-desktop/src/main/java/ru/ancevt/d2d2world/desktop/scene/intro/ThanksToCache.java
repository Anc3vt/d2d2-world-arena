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
package ru.ancevt.d2d2world.desktop.scene.intro;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.hash.MD5;
import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2world.net.file.FileDataUtils;

public class ThanksToCache {

    private static final String CACHE_DIRECTORY = "cache/";

    public static boolean contains(String fileName, long fileSize) {
        if (FileDataUtils.exists(CACHE_DIRECTORY + MD5.hash(fileName))) {
            long cachedFileSize = FileDataUtils.getSize(CACHE_DIRECTORY + MD5.hash(fileName));
            return cachedFileSize == fileSize;
        }
        return false;
    }

    public static @NotNull Texture getTextureFromCache(String fileName) {
        return D2D2.getTextureManager()
                .loadTextureAtlas(FileDataUtils.getInputStream(CACHE_DIRECTORY + MD5.hash(fileName)))
                .createTexture();
    }

    public static void saveToCache(String fileName, byte[] bytes) {
        FileDataUtils.directory(CACHE_DIRECTORY);
        FileDataUtils.save(CACHE_DIRECTORY + MD5.hash(fileName), bytes);
    }
}
