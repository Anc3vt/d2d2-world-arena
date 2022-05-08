/*
 *   D2D2 core
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
    void stop();

    void setVolume(float vaolume);
    float getVolume();
    void setPan(float pan);
    float getPan();

    static @NotNull Media lookupSound(String path) {
        Media media = medias.get(path);
        if (media == null) {
            media = new BlockingSound(path);
            medias.put(path, media);
        }
        return media;
    }

    static @NotNull Media lookupSoundAsset(String path) {
        Media media = medias.get(path);
        if (media == null) {
            media = new BlockingSound(Assets.getAssetAsStream(path));
            medias.put(':' + path, media);
        }
        return media;
    }

    static void main(String[] args) {
        while (true) {
            Media media = Media.lookupSound("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/builtin-mapkit/character-damage.ogg");
            media.play();
            new Lock().lock(250, TimeUnit.MILLISECONDS);
        }
    }
}
