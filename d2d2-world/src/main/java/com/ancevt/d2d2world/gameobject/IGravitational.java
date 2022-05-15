
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IGravitational extends IMovable {

    @Property
    default float getWeight() {
        return DefaultMaps.weightMap.getOrDefault(this, 0f);
    }

    @Property
    default void setWeight(float weight) {
        DefaultMaps.weightMap.put(this, weight);
    }

    default void setFloor(ICollision floor) {
        DefaultMaps.floorMap.put(this, floor);
    }

    default ICollision getFloor() {
        return DefaultMaps.floorMap.get(this);
    }

    default void setVelocityX(float velocityX) {
        DefaultMaps.velocityXMap.put(this, velocityX);
    }

    default void setVelocityY(float velocityY) {
        DefaultMaps.velocityYMap.put(this, velocityY);
    }

    default void setVelocity(float vX, float vY) {
        setVelocityX(vX);
        setVelocityY(vY);
    }

    default float getVelocityX() {
        return DefaultMaps.velocityXMap.getOrDefault(this, 0f);
    }

    default float getVelocityY() {
        return DefaultMaps.velocityYMap.getOrDefault(this, 0f);
    }

    @Property
    default void setGravityEnabled(boolean b) {
        DefaultMaps.gravityEnabledMap.put(this, b);
    }

    @Property
    default boolean isGravityEnabled() {
        return DefaultMaps.gravityEnabledMap.getOrDefault(this, false);
    }
}
