
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IMovable extends IResettable {

    @Property
    default void setStartX(float x) {
        DefaultMaps.startXMap.put(this, x);
    }

    @Property
    default void setStartY(float y) {
        DefaultMaps.startYMap.put(this, y);
    }

    default void setStartXY(float x, float y) {
        setStartX(x);
        setStartY(y);
    }

    @Property
    default float getStartX() {
        return DefaultMaps.startXMap.getOrDefault(this, 0f);
    }

    @Property
    default float getStartY() {
        return DefaultMaps.startYMap.getOrDefault(this, 0f);
    }

    default float getMovingSpeedX() {
        return DefaultMaps.movingSpeedXMap.getOrDefault(this, 0f);
    }

    default float getMovingSpeedY() {
        return DefaultMaps.movingSpeedYMap.getOrDefault(this, 0f);
    }

    default void setMovingSpeedX(float value) {
        DefaultMaps.movingSpeedXMap.put(this, value);
    }

    default void setMovingSpeedY(float value) {
        DefaultMaps.movingSpeedYMap.put(this, value);
    }
}
