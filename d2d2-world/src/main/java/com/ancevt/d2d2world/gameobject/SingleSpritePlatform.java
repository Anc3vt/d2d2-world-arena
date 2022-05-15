
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class SingleSpritePlatform extends Platform {

    public SingleSpritePlatform(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        add(new Sprite(mapkitItem.getTexture()));
    }
}
