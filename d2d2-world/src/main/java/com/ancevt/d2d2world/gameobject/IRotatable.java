
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IRotatable extends IGameObject {

    @Property
    void setRotation(float r);

    @Property
    float getRotation();

    default void rotate(float r) {
        setRotation(getRotation() + r);
    }
}
