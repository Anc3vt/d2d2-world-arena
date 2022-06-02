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

import com.ancevt.d2d2world.D2D2World;

public interface ISynchronized extends IGameObject {

    default void sync() {
        if (!isPermanentSync() && isOnWorld() && D2D2World.isServer()) {
            getWorld().getSyncDataAggregator().createSyncDataOf(this);
        }
    }

    default void setPermanentSync(boolean permanentSync) {
        DefaultMaps.permanentSyncMap.putIfAbsent(this, permanentSync);
    }

    default boolean isPermanentSync() {
        return DefaultMaps.permanentSyncMap.get(this);
    }
}
