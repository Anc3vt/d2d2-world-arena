
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IColored extends IGameObject {

    @Property
    void setColorHex(String colorHex);

    @Property
    String getColorHex();
}
