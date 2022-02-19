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
import ru.ancevt.d2d2.common.PlainRect;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObject;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.EventListener;
import ru.ancevt.d2d2world.gameobject.ICollision;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.gameobject.IRepeatable;
import ru.ancevt.d2d2world.gameobject.area.Area;

public class Selection extends DisplayObjectContainer implements EventListener {
    private final BorderedRect rect;

    private final IGameObject gameObject;
    private RepeatControl repeatControl;

    public Selection(final IGameObject gameObject) {
        this.gameObject = gameObject;
        rect = new BorderedRect(1, 1);
        rect.setFillColor(Color.BLUE);
        rect.setBorderColor(Color.BLACK);
        rect.setAlpha(0.25f);

        final DisplayObject displayObject = (DisplayObject) gameObject;
        setSize(displayObject.getWidth(), displayObject.getHeight());

        add(rect);

        if (gameObject instanceof IRepeatable || gameObject instanceof Area) {
            repeatControl = new RepeatControl();
            add(repeatControl, getWidth() - 8, getHeight() - 8);
        }

        addEventListener(Event.EACH_FRAME, this);
    }

    public final void setWidth(final float width) {
        rect.setWidth(width);
    }

    public final void setHeight(final float height) {
        rect.setHeight(height);
    }

    @Override
    public float getWidth() {
        return rect.getWidth();
    }

    @Override
    public float getHeight() {
        return rect.getHeight();
    }

    public final void setSize(final float w, final float h) {
        setWidth(w);
        setHeight(h);
    }

    public final IGameObject getGameObject() {
        return gameObject;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType().equals(Event.EACH_FRAME)) {
            setXY(
                    gameObject.getX(),
                    gameObject.getY()
            );

            if (gameObject instanceof ICollision c) {
                move(c.getCollisionX(), c.getCollisionY());
            }

            setSize(
                    gameObject.getWidth(),
                    gameObject.getHeight()
            );

            if (repeatControl != null && repeatControl.hasParent()) {
                repeatControl.setXY(getWidth() - 8, getHeight() - 8);
//			final float parentScale = getParent().getAbsoluteScaleX();
//			repeatControl.setScale(1f / parentScale, 1f / parentScale);
            }
        }
    }

    public static class RepeatControl extends DisplayObjectContainer {

        private final PlainRect rect, shadow;

        public RepeatControl() {
            shadow = new PlainRect(10, 10);
            shadow.setColor(Color.BLACK);

            add(shadow, -1, -1);

            rect = new PlainRect(8, 8);
            rect.setColor(Color.WHITE);
            add(rect);

        }

    }
}


