
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.control.Controller;

public interface IControllable extends IGameObject {

    default void setController(final Controller controller) {
        DefaultMaps.controllerMap.put(this, controller);
    }

    default Controller getController() {
        return DefaultMaps.controllerMap.get(this);
    }
}
