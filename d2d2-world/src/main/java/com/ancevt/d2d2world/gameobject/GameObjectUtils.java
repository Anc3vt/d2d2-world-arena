
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.world.Layer;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.data.Properties.setProperties;

public class GameObjectUtils {

    public static IGameObject copy(IGameObject of, int newGameObjectId) {
        IGameObject result = of.getMapkitItem().createGameObject(newGameObjectId);
        return (IGameObject) setProperties(result, getProperties(of, DataEntry.newInstance()));
    }

    public static int getLayerIndex(IGameObject gameObject) {
        if (!gameObject.hasParent()) throw new IllegalStateException("Game object has no parent");

        return ((Layer) gameObject.getParent()).getIndex();
    }

}