
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface ISizable extends IGameObject {


    @Property
    void setWidth(float width);

    @Property
    float getWidth();

    @Property
    void setHeight(float height);


    @Property
    float getHeight();

    default void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }
}
