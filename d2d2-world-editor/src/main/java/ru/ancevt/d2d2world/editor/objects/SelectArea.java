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
package ru.ancevt.d2d2world.editor.objects;

import ru.ancevt.d2d2.common.BorderedRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.event.Event;

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
