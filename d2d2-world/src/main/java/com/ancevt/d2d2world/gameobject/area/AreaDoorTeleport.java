
package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class AreaDoorTeleport extends Area {

    public static final Color FILL_COLOR = Color.BLUE;
    private static final Color STROKE_COLOR = Color.WHITE;

    private String targetAreaName;

    public AreaDoorTeleport(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setFillColor(FILL_COLOR);
        setBorderColor(STROKE_COLOR);
    }

    @Property
    public void setTargetAreaName(String targetAreaName) {
        this.targetAreaName = targetAreaName;
    }

    @Property
    public String getTargetAreaName() {
        return targetAreaName;
    }
}




















