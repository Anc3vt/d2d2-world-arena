
package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class AreaHook extends Area {

    public static final Color FILL_COLOR = Color.LIGHT_GREEN;
    private static final Color STROKE_COLOR = Color.WHITE;

    public AreaHook(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setBorderColor(STROKE_COLOR);
        setFillColor(FILL_COLOR);
    }


}
