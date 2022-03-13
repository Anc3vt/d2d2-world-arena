package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.mapkit.MapkitItem;

public class TestPlatform extends Platform {

    public TestPlatform(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setActionProgramData("16 moveX 0.5f;16 moveX -0.5f;");
    }
}
