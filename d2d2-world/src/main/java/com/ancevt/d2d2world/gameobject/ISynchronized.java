package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.D2D2World;

public interface ISynchronized extends IGameObject {

    default void sync() {
        if (!isPermanentSync() && isOnWorld() && D2D2World.isServer()) {
            getWorld().getSyncDataAggregator().createSyncDataOf(this);
        }
    }

    void setPermanentSync(boolean permanentSync);

    boolean isPermanentSync();
}
