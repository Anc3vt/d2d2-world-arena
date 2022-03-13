package com.ancevt.d2d2world.sync;

import com.ancevt.d2d2world.gameobject.IAnimated;
import com.ancevt.d2d2world.gameobject.IDestroyable;
import com.ancevt.d2d2world.gameobject.IDirectioned;
import com.ancevt.d2d2world.gameobject.IGameObject;

public interface ISyncDataAggregator {

    void newGameObject(IGameObject gameObject);

    void xy(IGameObject gameObject);

    void animation(IAnimated animated, boolean loop);

    void health(IDestroyable destroyable);

    void maxHealth(IDestroyable destroyable);

    void direction(IDirectioned directioned);

    void remove(IGameObject gameObject);

    byte[] createSyncMessage(IGameObject gameObject);

    byte[] createSyncMessage();

    boolean hasData();
}
