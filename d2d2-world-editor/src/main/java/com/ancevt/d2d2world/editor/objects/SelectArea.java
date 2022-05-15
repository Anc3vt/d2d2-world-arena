
package com.ancevt.d2d2world.editor.objects;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.event.Event;

public class SelectArea extends BorderedRect {

    private static final Color STROKE_COLOR = Color.GRAY;

    public SelectArea() {
        super(10f, 10f);
        setFillColor(null);

        setBorderColor(STROKE_COLOR);

        addEventListener(Event.EACH_FRAME, e -> setBorderWidth(1 / getAbsoluteScaleX()));
    }

    public void setXY(SelectRectangle selectRectangle) {
        setXY(selectRectangle.getX1(), selectRectangle.getY1());
        setSize(selectRectangle.getWidth(), selectRectangle.getHeight());
    }
}
