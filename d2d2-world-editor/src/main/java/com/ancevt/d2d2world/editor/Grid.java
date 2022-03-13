/*
 *   D2D2 World Editor
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2world.editor;

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

        final float w = D2D2.getStage().getWidth() / getAbsoluteScaleX();
        final float h = D2D2.getStage().getHeight() / getAbsoluteScaleY();

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
            float w = D2D2.getStage().getWidth() + SIZE * 2;
            float h = D2D2.getStage().getHeight() + SIZE * 2;

            switch (orientation) {
                case HORIZONTAL -> setScale(w / scale, 1.0f / scale);
                case VERTICAL -> setScale(1.0f / scale, h / scale);
            }
        }

    }
}


