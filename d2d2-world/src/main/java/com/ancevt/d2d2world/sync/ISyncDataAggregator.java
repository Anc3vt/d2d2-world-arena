package com.ancevt.d2d2world.sync;

import com.ancevt.d2d2world.gameobject.*;
import org.jetbrains.annotations.NotNull;

public interface ISyncDataAggregator {

    byte[] EMPTY_ARRAY = new byte[]{};

    default void weapon(Actor actor) {

    }

    default void newGameObject(IGameObject gameObject) {
    }

    default void actionIndex(IActioned actioned) {
    }

    default void repair(@NotNull IDestroyable destroyable) {
    }

    default void xy(IGameObject gameObject) {
    }

    default void animation(IAnimated animated, boolean loop) {
    }

    default void health(IDestroyable destroyable, IDamaging damaging) {
    }

    default void maxHealth(IDestroyable destroyable) {
    }

    default void direction(IDirectioned directioned) {
    }

    default void remove(IGameObject gameObject) {
    }

    default void aim(@NotNull Actor actor) {
    }

    default byte[] pullSyncDataMessage() {
        return EMPTY_ARRAY;
    }

    default boolean hasData() {
        return false;
    }

    default void visibility(@NotNull IGameObject gameObject, boolean value) {
    }

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
}
