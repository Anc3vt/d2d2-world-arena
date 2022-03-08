package ru.ancevt.d2d2world.sync;

import ru.ancevt.commons.exception.NotImplementedException;
import ru.ancevt.d2d2world.gameobject.IAnimated;
import ru.ancevt.d2d2world.gameobject.IDestroyable;
import ru.ancevt.d2d2world.gameobject.IDirectioned;
import ru.ancevt.d2d2world.gameobject.IGameObject;

public class StubSyncManager implements ISyncManager {

    @Override
    public void newGameObject(IGameObject gameObject) {

    }

    @Override
    public void xy(IGameObject gameObject) {

    }

    @Override
    public void animation(IAnimated animated, boolean loop) {

    }

    @Override
    public void health(IDestroyable destroyable) {

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
    public byte[] createSyncMessage(IGameObject gameObject) {
        throw new NotImplementedException();
    }

    @Override
    public byte[] createSyncMessage() {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasData() {
        return false;
    }
}
