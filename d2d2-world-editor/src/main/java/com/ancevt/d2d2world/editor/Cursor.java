
package com.ancevt.d2d2world.editor;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.mapkit.AreaMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class Cursor extends Sprite {

    private MapkitItem mapkitItem;

    public Cursor() {
        setAlpha(0.5f);
    }

    public void setMapKitItem(MapkitItem mapkitItem) {
        this.mapkitItem = mapkitItem;

        if (mapkitItem != null) {
            setTexture(mapkitItem.getIcon().getTexture());

            if (mapkitItem.getMapkit() instanceof AreaMapkit) {
                setScale(10f, 10f);
                setColor(mapkitItem.getIcon().getColor());
            } else {
                setScale(1f, 1f);
                setColor(Color.WHITE);
            }

            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }
}
