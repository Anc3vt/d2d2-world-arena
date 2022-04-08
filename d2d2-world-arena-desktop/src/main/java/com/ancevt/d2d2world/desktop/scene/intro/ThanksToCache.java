/*
 *   D2D2 World Arena Desktop
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2world.desktop.scene.intro;

import org.jetbrains.annotations.NotNull;
import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.data.file.FileSystem;

public class ThanksToCache {

    private static final String CACHE_DIRECTORY = "data/cache/";

    public static boolean contains(String fileName, long fileSize) {
        if (FileSystem.exists(CACHE_DIRECTORY + MD5.hash(fileName))) {
            long cachedFileSize = FileSystem.getSize(CACHE_DIRECTORY + MD5.hash(fileName));
            return cachedFileSize == fileSize;
        }
        return false;
    }

    public static @NotNull Texture getTextureFromCache(String fileName) {
        return D2D2.getTextureManager()
                .loadTextureAtlas(FileSystem.getInputStream(CACHE_DIRECTORY + MD5.hash(fileName)))
                .createTexture();
    }

    public static void saveToCache(String fileName, byte[] bytes) {
        FileSystem.directory(CACHE_DIRECTORY);
        FileSystem.save(CACHE_DIRECTORY + MD5.hash(fileName), bytes);
    }
}
