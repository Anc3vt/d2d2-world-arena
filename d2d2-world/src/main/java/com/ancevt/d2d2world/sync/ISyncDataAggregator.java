package com.ancevt.d2d2world.sync;

import com.ancevt.d2d2world.gameobject.*;
import org.jetbrains.annotations.NotNull;

public interface ISyncDataAggregator {

    byte[] EMPTY_ARRAY = new byte[]{};

    void newGameObject(IGameObject gameObject);

    void armDegree(Actor actor);

    void actionIndex(IActioned actioned);

    void repair(@NotNull IDestroyable destroyable);

    void xy(IGameObject gameObject);

    void animation(IAnimated animated, boolean loop);

    void health(IDestroyable destroyable, IDamaging damaging);

    void maxHealth(IDestroyable destroyable);

    void direction(IDirectioned directioned);

    void remove(IGameObject gameObject);

    byte[] pullSyncDataMessage();

    boolean hasData();

    void visibility(@NotNull IGameObject gameObject, boolean value);

    default void createSyncDataOf(IGameObject o) {
        if (!(o instanceof ISynchronized)) return;

        newGameObject(o);
        xy(o);
        if (o instanceof IAnimated a) {
            animation(a, true);
        }
        if (o instanceof IDirectioned d) {
            direction(d);
        }
        if (o instanceof IDestroyable d) {
            health(d, null);
            maxHealth(d);
        }
    }

    void aim(@NotNull Actor actor);
}
