package com.ancevt.d2d2.media;

import com.ancevt.commons.concurrent.Lock;
import com.ancevt.d2d2.asset.Assets;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface Media {

    Map<String, Media> medias = new HashMap<>();

    void play();

    static @NotNull Media createSound(String path) {
        Media media = medias.get(path);
        if (media == null) {
            media = new BlockingSound(path);
            medias.put(path, media);
        }
        return media;
    }

    static @NotNull Media createSoundAsset(String path) {
        Media media = medias.get(path);
        if (media == null) {
            media = new BlockingSound(Assets.getAssetAsStream(path));
            medias.put(':' + path, media);
        }
        return media;
    }

    static void main(String[] args) {
        while (true) {
            Media media = Media.createSound("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/builtin-mapkit/character-damage.ogg");
            media.play();
            new Lock().lock(250, TimeUnit.MILLISECONDS);
        }
    }
}
