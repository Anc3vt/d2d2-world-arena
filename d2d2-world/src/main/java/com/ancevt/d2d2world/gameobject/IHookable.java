
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.gameobject.area.AreaHook;

public interface IHookable extends IGravitational, ICollision {

    default void setHook(final AreaHook hook) {
        DefaultMaps.hookMap.put(this, hook);
    }

    default AreaHook getHook() {
        return DefaultMaps.hookMap.get(this);
    }
}
