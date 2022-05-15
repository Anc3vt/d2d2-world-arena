
package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.gameobject.ITight;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class AreaCollision extends Area implements ITight {

    public static final Color FILL_COLOR = Color.BLACK;
    private static final Color FILL_FLOOR_ONLY_COLOR = Color.WHITE;
    private static final Color STROKE_COLOR = Color.WHITE;

    private boolean floorOnly;

    private boolean pushable;

    public AreaCollision(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setFillColor(FILL_COLOR);
        setBorderColor(STROKE_COLOR);
    }

    @Override
    public void setFloorOnly(boolean b) {
        floorOnly = b;
        setFillColor(floorOnly ? FILL_FLOOR_ONLY_COLOR : FILL_COLOR);
    }

    @Override
    public boolean isFloorOnly() {
        return floorOnly;
    }

    @Override
    public void setPushable(boolean pushable) {
        this.pushable = pushable;
    }

    @Override
    public boolean isPushable() {
        return pushable;
    }
}
