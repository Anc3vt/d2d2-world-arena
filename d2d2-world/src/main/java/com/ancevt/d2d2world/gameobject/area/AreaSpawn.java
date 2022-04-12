package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class AreaSpawn extends Area {

    public static final Color FILL_COLOR = Color.ORANGE;
    private static final Color STROKE_COLOR = Color.WHITE;
    private boolean enabled;

    public AreaSpawn(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setTextVisible(true);
        setText("spawn");
        setBorderColor(STROKE_COLOR);
        setFillColor(FILL_COLOR);
        setEnabled(true);
    }

    @Property
    public void setEnabled(boolean b) {
        enabled = b;
    }

    @Property
    public boolean isEnabled() {
        return enabled;
    }
}
