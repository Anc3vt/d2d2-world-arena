/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
