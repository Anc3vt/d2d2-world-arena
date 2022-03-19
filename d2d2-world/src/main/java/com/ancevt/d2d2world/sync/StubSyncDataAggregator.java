package com.ancevt.d2d2world.sync;

import com.ancevt.d2d2world.gameobject.*;
import org.jetbrains.annotations.NotNull;

public class StubSyncDataAggregator implements ISyncDataAggregator {

    private static final byte[] empty = new byte[]{};


    @Override
    public void actionIndex(IActioned actioned) {

    }

    @Override
    public void newGameObject(IGameObject gameObject) {

    }

    @Override
    public void repair(@NotNull IDestroyable destroyable) {

    }

    @Override
    public void xy(IGameObject gameObject) {

    }

    @Override
    public void animation(IAnimated animated, boolean loop) {

    }

    @Override
    public void health(IDestroyable destroyable, IDamaging damaging) {

    }

    @Override
    public void maxHealth(IDestroyable destroyable) {

    }

    @Override
    public void direction(IDirectioned directioned) {

    }

    @Override
    public void remove(IGameObject gameObject) {

    }

    @Override
    public byte[] pullSyncDataMessage() {
        return empty;
    }

    @Override
    public boolean hasData() {
        return false;
    }

    @Override
    public void visibility(@NotNull IGameObject gameObject, boolean value) {

    }
}
