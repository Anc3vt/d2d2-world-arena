
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IAlphable {

    @Property
    void setAlpha(float alpha);

    @Property
    float getAlpha();
}
