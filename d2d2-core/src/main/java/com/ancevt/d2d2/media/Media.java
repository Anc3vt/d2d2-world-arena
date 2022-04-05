package com.ancevt.d2d2.media;

import com.ancevt.commons.concurrent.Lock;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface Media {

    Map<String, Media> medias = new HashMap<>();

    void play();

    static Media playSound(String path) {
        Media media = medias.get(path);
        if (media == null) {
            try {
                media = new SoundImpl2(path);
                medias.put(path, media);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        media.play();
        return media;
    }

    static void main(String[] args) {
        while (true) {
            System.out.println("l {");
            Media media = Media.playSound("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/builtin-mapkit/character-damage.ogg");
            media.play();
            new Lock().lock(250, TimeUnit.MILLISECONDS);
            System.out.println("} l");
        }
    }
}
