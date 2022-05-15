
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
