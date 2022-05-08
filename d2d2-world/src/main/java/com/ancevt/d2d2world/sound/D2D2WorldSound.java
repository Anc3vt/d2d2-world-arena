/*
 *   D2D2 World
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
package com.ancevt.d2d2world.sound;

import com.ancevt.d2d2world.math.RadialUtils;
import com.ancevt.d2d2world.world.Camera;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class D2D2WorldSound {

    public final static String PLAYER_ENTER = "sound/d2d2world/player-enter.ogg";
    public final static String PLAYER_EXIT = "sound/d2d2world/player-exit.ogg";
    public final static String PLAYER_SPAWN = "sound/d2d2world/player-spawn.ogg";

    // path -> time millis
    private static final Map<String, Long> sounds = new HashMap<>();

    public static void playSoundAsset(String d2d2WorldSound, Camera camera, float x, float y) {
        SoundMachine.getInstance().playAsset(d2d2WorldSound, 1f, 0f); // TODO: compute pan and vol
    }

    public static void playSoundAsset(String d2d2WorldSound) {
        SoundMachine.getInstance().playAsset(d2d2WorldSound, 1f, 0f);
    }

    public static void playSound(String path, @NotNull Camera camera, float x, float y) {
        float distance = RadialUtils.distance(x, y, camera.getX(), camera.getY());
        float volume = (400f - distance) / 10;

        float pan = (x - camera.getX()) / 400;

        SoundMachine.getInstance().play(path, volume, pan);
    }

    public static void playSound(String path) {
        SoundMachine.getInstance().play(path, 1f, 0f);
        sounds.put(path, System.currentTimeMillis());
    }

    public static void playSoundDoubleSafe(String path, Camera camera, float x, float y) {
        long time = sounds.getOrDefault(path, 0L);
        long diff = System.currentTimeMillis() - time;

        if(diff > 250) {
            playSound(path, camera, x, y);
            sounds.put(path, System.currentTimeMillis());
        }

    }
}
