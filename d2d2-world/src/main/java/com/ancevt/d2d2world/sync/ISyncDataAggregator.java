package com.ancevt.d2d2world.sync;

import com.ancevt.d2d2world.gameobject.*;
import org.jetbrains.annotations.NotNull;

public interface ISyncDataAggregator {

    void newGameObject(IGameObject gameObject);

    void repair(@NotNull IDestroyable destroyable);

    void xy(IGameObject gameObject);

    void animation(IAnimated animated, boolean loop);

    void health(IDestroyable destroyable, IDamaging damaging);

    void maxHealth(IDestroyable destroyable);

    void direction(IDirectioned directioned);

    void remove(IGameObject gameObject);

    byte[] createSyncMessage(IGameObject gameObject);

    byte[] createSyncMessage();

    boolean hasData();

    void visibility(@NotNull IGameObject gameObject, boolean value);
}
