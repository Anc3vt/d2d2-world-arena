
package com.ancevt.d2d2world.ui;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IColored;
import com.ancevt.d2d2.event.Event;

import java.util.ArrayList;
import java.util.List;

public class Grid extends DisplayObjectContainer implements IColored {

    public static final int SIZE = 16;

    private static final float ALPHA = 0.075f;
    public static final int DEFAULT_COLOR = 0xFFFFFF;

    private final List<Line> lines;
    private Color color;

    public Grid() {
        lines = new ArrayList<>();
        setColor(DEFAULT_COLOR);
        setAlpha(0.25f);

        addEventListener(Event.EACH_FRAME, this::eachFrame);
    }

    private void eachFrame(Event event) {
        while (getAbsoluteX() < -16) moveX(16);
        while (getAbsoluteY() < -16) moveY(16);
        while (getAbsoluteX() > 0) moveX(-16);
        while (getAbsoluteY() > 0) moveY(-16);
    }

    public final void redrawLines() {
        recreate(color);
    }

    private void recreate(Color color) {
        removeAllChildren();

        final float w = D2D2.stage().getWidth() / getAbsoluteScaleX();
        final float h = D2D2.stage().getHeight() / getAbsoluteScaleY();

        for (float x = 0; x < w + SIZE * 2; x += SIZE) {
            Line line = new Line(this, Line.VERTICAL);
            line.setColor(color);
            line.setX(x);
            line.setAlpha(ALPHA);
            add(line);
            lines.add(line);
        }

        for (float y = 0; y < h + SIZE * 2; y += SIZE) {
            Line line = new Line(this, Line.HORIZONTAL);
            line.setColor(color);
            line.setY(y);
            line.setAlpha(ALPHA);
            add(line);
            lines.add(line);
        }
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        recreate(color);
    }

    @Override
    public Color getColor() {
        return lines.get(0).getColor();
    }

    @Override
    public void setColor(int rgb) {
        setColor(new Color(rgb));
    }

    private static class Line extends PlainRect {

        public static final byte HORIZONTAL = 0x00;
        public static final byte VERTICAL = 0x01;

        public Line(Grid grid, byte orientation) {
            super(1.0f, 1.0f);
            float scale = grid.getAbsoluteScaleX();
            float w = D2D2.stage().getWidth() + SIZE * 2;
            float h = D2D2.stage().getHeight() + SIZE * 2;

            switch (orientation) {
                case HORIZONTAL -> setScale(w / scale, 2f / scale);
                case VERTICAL -> setScale(2f / scale, h / scale);
            }
        }

    }
}


