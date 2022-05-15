
package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class AreaTarget extends Area {

    public static final Color FILL_COLOR = Color.PINK;
    private static final Color STROKE_COLOR = Color.WHITE;

    public AreaTarget(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setFillColor(FILL_COLOR);
        setBorderColor(STROKE_COLOR);
    }
}




















