package com.ancevt.d2d2world.sync;

import com.ancevt.d2d2world.gameobject.IDestroyable;

public interface ISyncClientDataSender {
    byte[] EMPTY_ARRAY = new byte[]{};

    default void health(IDestroyable destroyable) {
    }

    default byte[] pullSyncDataMessage() {
        return EMPTY_ARRAY;
    }


}
