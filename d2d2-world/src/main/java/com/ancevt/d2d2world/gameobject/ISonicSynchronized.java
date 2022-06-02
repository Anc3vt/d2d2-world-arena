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

package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.map.MapIO;
import com.ancevt.d2d2world.sound.D2D2WorldSound;

public interface ISonicSynchronized extends IGameObject {

    default void playSound(String soundFilenameFromMapkit) {
        if (isOnWorld()) {
            String mapkitName = getMapkitItem().getMapkit().getName();
            String path = MapIO.getMapkitsDirectory() + mapkitName + "/" + soundFilenameFromMapkit;
            D2D2WorldSound.playSoundDoubleSafe(path, getWorld().getCamera(), getX(), getY());
            getWorld().getSyncDataAggregator().sound(this, soundFilenameFromMapkit);
        }
    }

    default void playSoundFromServer(String soundFilenameFromMapkit) {
        if (isOnWorld()) {
            String mapkitName = getMapkitItem().getMapkit().getName();
            String path = MapIO.getMapkitsDirectory() + mapkitName + "/" + soundFilenameFromMapkit;
            D2D2WorldSound.playSoundDoubleSafe(path, getWorld().getCamera(), getX(), getY());
        }
    }
}
