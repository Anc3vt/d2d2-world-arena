
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface ISpeedable extends IGameObject {

    @Property
    default void setSpeed(float speed) {
        DefaultMaps.speedMap.put(this, speed);
    }

    @Property
    default float getSpeed() {
        return DefaultMaps.speedMap.getOrDefault(this, 0f);
    }
}
