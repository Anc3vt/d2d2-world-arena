
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface ITight extends ICollision {

    @Property
    default void setFloorOnly(boolean b) {
        DefaultMaps.floorOnlyMap.put(this, b);
    }

    @Property
    default boolean isFloorOnly() {
        return DefaultMaps.floorOnlyMap.getOrDefault(this, false);
    }

    @Property
    default void setPushable(boolean b) {
        DefaultMaps.pushableMap.put(this, b);
    }

    @Property
    default boolean isPushable() {
        return DefaultMaps.pushableMap.getOrDefault(this, false);
    }
}
