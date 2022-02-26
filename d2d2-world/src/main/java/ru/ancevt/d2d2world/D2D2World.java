package ru.ancevt.d2d2world;

import ru.ancevt.d2d2.D2D2;

public class D2D2World {

    private D2D2World() {
    }

    public static void init() {
        D2D2.getTextureManager().loadTextureDataInfo("d2d2-world-common-texture-data.inf");
    }
}
