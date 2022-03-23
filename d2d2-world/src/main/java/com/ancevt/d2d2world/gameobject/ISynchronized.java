package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.D2D2World;

import java.util.HashMap;
import java.util.Map;

public interface ISynchronized extends IGameObject {

    Map<ISynchronized, Boolean> permanentSyncMap = new HashMap<>();

    default void sync() {
        if (!isPermanentSync() && isOnWorld() && D2D2World.isServer()) {
            getWorld().getSyncDataAggregator().createSyncDataOf(this);
        }
    }

    default void setPermanentSync(boolean permanentSync) {
        permanentSyncMap.putIfAbsent(this, permanentSync);
    }

    default boolean isPermanentSync() {
        return permanentSyncMap.get(this);
    }
}
