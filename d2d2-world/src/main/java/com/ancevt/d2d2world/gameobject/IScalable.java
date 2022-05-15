
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IScalable {

    @Property
    void setScaleX(float scale);

    @Property
    void setScaleY(float scale);

    @Property
    float getScaleX();

    @Property
    float getScaleY();

    void setScale(float scaleX, float scaleY);
}
