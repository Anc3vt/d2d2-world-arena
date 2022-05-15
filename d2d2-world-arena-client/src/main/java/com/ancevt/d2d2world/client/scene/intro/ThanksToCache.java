
package com.ancevt.d2d2world.client.scene.intro;

import org.jetbrains.annotations.NotNull;
import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.data.file.FileSystemUtils;

public class ThanksToCache {

    private static final String CACHE_DIRECTORY = "data/cache/";

    public static boolean contains(String fileName, long fileSize) {
        if (FileSystemUtils.exists(CACHE_DIRECTORY + MD5.hash(fileName))) {
            long cachedFileSize = FileSystemUtils.getSize(CACHE_DIRECTORY + MD5.hash(fileName));
            return cachedFileSize == fileSize;
        }
        return false;
    }

    public static @NotNull Texture getTextureFromCache(String fileName) {
        return D2D2.getTextureManager()
                .loadTextureAtlas(FileSystemUtils.getInputStream(CACHE_DIRECTORY + MD5.hash(fileName)))
                .createTexture();
    }

    public static void saveToCache(String fileName, byte[] bytes) {
        FileSystemUtils.directory(CACHE_DIRECTORY);
        FileSystemUtils.save(CACHE_DIRECTORY + MD5.hash(fileName), bytes);
    }
}
