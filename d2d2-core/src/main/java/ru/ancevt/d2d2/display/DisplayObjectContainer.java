/*
 *   D2D2 core
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
package ru.ancevt.d2d2.display;

import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.EventPool;

import java.util.ArrayList;
import java.util.List;

public class DisplayObjectContainer extends DisplayObject implements IDisplayObjectContainer {

    static final int MAX_X = 1048576;
    static final int MAX_Y = 1048576;

    final List<IDisplayObject> children;

    public DisplayObjectContainer() {
        children = new ArrayList<>();
    }

    @Override
    public void add(IDisplayObject child) {
        ((DisplayObject) child).setParent(this);
        child.dispatchEvent(EventPool.createEvent(Event.ADD, child, this));
        children.remove(child);
        children.add(child);

        if (this.getStage() != null) Stage.dispatchAddToStage(child);
    }

    @Override
    public void add(IDisplayObject child, int index) {
        ((DisplayObject) child).setParent(this);
        child.dispatchEvent(EventPool.createEvent(Event.ADD, child, this));

        children.remove(child);
        children.add(index, child);

        if (this.getStage() != null) Stage.dispatchAddToStage(child);
    }

    @Override
    public void add(IDisplayObject child, float x, float y) {
        child.setXY(x, y);
        ((DisplayObject) child).setParent(this);
        child.dispatchEvent(EventPool.createEvent(Event.ADD, child, this));

        children.remove(child);
        children.add(child);

        if (this.getStage() != null) Stage.dispatchAddToStage(child);

    }

    @Override
    public void add(IDisplayObject child, int index, float x, float y) {
        child.setXY(x, y);
        ((DisplayObject) child).setParent(this);
        child.dispatchEvent(EventPool.createEvent(Event.ADD, child, this));

        children.remove(child);
        children.add(index, child);

        if (this.getStage() != null) Stage.dispatchAddToStage(child);
    }

    @Override
    public void remove(IDisplayObject child) {
        final boolean removedFromStage = child.getStage() != null;

        ((DisplayObject) child).setParent(null);
        child.dispatchEvent(EventPool.createEvent(Event.REMOVE, child, this));

        children.remove(child);

        if (removedFromStage) Stage.dispatchRemoveFromStage(child);
    }



    @Override
    public int indexOf(IDisplayObject child) {
        return children.indexOf(child);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public IDisplayObject getChild(int index) {
        return children.get(index);
    }

    @Override
    public void removeAllChildren() {
        children.clear();
    }

    @Override
    public boolean contains(IDisplayObject child) {
        return children.contains(child);
    }

    /*
    final void actualRemove(IDisplayObject child) {
        children.remove(child);
    }

    final void actualRemove(int index) {
        children.remove(index);
    }

    final void actualAdd(IDisplayObject child) {
        children.add(child);
    }

    final void actualAdd(IDisplayObject child, int index) {
        children.add(index, child);
    }
     */

    @Override
    public void onEachFrame() {
        // For overriding
    }

    @Override
    public float getWidth() {
        float min = MAX_X;
        float max = 0;

        for (final IDisplayObject child : children) {
            float x = child.getX();
            float xw = x + child.getWidth();

            min = Math.min(x, min);
            max = Math.max(xw, max);
        }

        return max - min;
    }

    @Override
    public float getHeight() {
        float min = MAX_Y;
        float max = 0;

        for (final IDisplayObject child : children) {

            final float y = child.getY();
            final float yh = y + child.getHeight();

            min = Math.min(y, min);
            max = Math.max(yh, max);
        }

        return max - min;
    }
}


































