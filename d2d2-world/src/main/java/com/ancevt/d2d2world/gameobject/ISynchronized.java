package com.ancevt.d2d2world.gameobject;

import static com.ancevt.d2d2world.D2D2World.isServer;

public interface ISynchronized extends IGameObject {

    default void sync() {
        if (!isPermanentSync() && isOnWorld() && isServer()) {
            getWorld().getSyncDataAggregator().createSyncDataOf(this);
        }
    }

    void setPermanentSync(boolean permanentSync);

    boolean isPermanentSync();
}
