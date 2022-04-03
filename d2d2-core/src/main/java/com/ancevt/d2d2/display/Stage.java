/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2.display;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventPool;

public class Stage extends DisplayObjectContainer {

    private static final String USE_SET_ROOT_INSTEAD_MESSAGE = "use setRoot instead";

    private int scaleMode;
    private int width;
    private int height;
    private int stageWidth;
    private int stageHeight;

    private Root root;

    public Stage() {
        setScaleMode(ScaleMode.REAL);
    }

    public void onResize(int width, int height) {
        this.width = width;
        this.height = height;

        if (root == null) return;

        float w = getWidth();
        float h = getHeight();
        float sW = getStageWidth();
        float sH = getStageHeight();
        float ratio = sW / sH;

        switch (scaleMode) {
            case ScaleMode.REAL -> {
                root.setScale(1f, 1f);
                dispatchEvent(EventPool.simpleEventSingleton(Event.RESIZE, this));
            }
            case ScaleMode.EXTENDED -> {
                root.setScale(w / sW, h / sH);
            }
            case ScaleMode.FIT -> {
                if (w / h < ratio) {
                    root.setScaleX(w / sW);
                    root.setScaleY(root.getScaleX() / ratio);
                } else {
                    root.setScaleY(h / sH);
                    root.setScaleX(root.getScaleY() * ratio);
                }
            }
            case ScaleMode.OUTFIT -> {
                if (w / h > ratio) {
                    root.setScaleX(w / sW);
                    root.setScaleY(root.getScaleX() / ratio);
                } else {
                    root.setScaleY(h / sH);
                    root.setScaleX(root.getScaleY() * ratio);
                }
            }
            case ScaleMode.AUTO -> {
                if (w > sW && h > sH) {
                    root.setScale(1f, 1f);
                } else {
                    if (w / h < ratio) {
                        root.setScaleX(w / sW);
                        root.setScaleY(root.getScaleX() / ratio);
                    } else {
                        root.setScaleY(h / sH);
                        root.setScaleX(root.getScaleY() * ratio);
                    }
                }
            }
            default -> throw new IllegalArgumentException("illegal scale mode provided: " + scaleMode);

        }
    }

    public int getScaleMode() {
        return scaleMode;
    }

    public void setScaleMode(int scaleMode) {
        this.scaleMode = scaleMode;
        onResize(width, height);
    }

    public void setStageWidth(int value) {
        this.stageWidth = value;
    }

    public float getStageWidth() {
        return stageWidth;
    }

    public void setStageHeight(int value) {
        this.stageHeight = value;
    }

    public float getStageHeight() {
        return stageHeight;
    }

    public void setStageSize(int width, int height) {
        this.stageWidth = width;
        this.stageHeight = height;
    }

    @Override
    public String toString() {
        return "Stage{" +
                "scaleMode=" + scaleMode +
                ", width=" + width +
                ", height=" + height +
                ", stageWidth=" + stageWidth +
                ", stageHeight=" + stageHeight +
                ", root=" + root +
                '}';
    }

    @Override
    public void add(@NotNull IDisplayObject child) {
        throw new UnsupportedOperationException(USE_SET_ROOT_INSTEAD_MESSAGE);
    }

    @Override
    public void add(@NotNull IDisplayObject child, float x, float y) {
        throw new UnsupportedOperationException(USE_SET_ROOT_INSTEAD_MESSAGE);
    }

    @Override
    public void add(@NotNull IDisplayObject child, int indexAt) {
        throw new UnsupportedOperationException(USE_SET_ROOT_INSTEAD_MESSAGE);
    }

    @Override
    public void add(@NotNull IDisplayObject child, int indexAt, float x, float y) {
        throw new UnsupportedOperationException(USE_SET_ROOT_INSTEAD_MESSAGE);
    }

    @Override
    public void remove(@NotNull IDisplayObject child) {
        throw new UnsupportedOperationException(USE_SET_ROOT_INSTEAD_MESSAGE);
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public Root getRoot() {
        return root;
    }

    public void setRoot(Root root) {
        if (this.root == root) return;

        Root oldRoot = this.root;

        if (oldRoot != null) {
            super.remove(oldRoot);
            dispatchRemoveFromStage(oldRoot);
        }

        this.root = root;

        super.add(root);
        dispatchAddToStage(root);

        onResize(width, height);
    }

    static void dispatchAddToStage(@NotNull IDisplayObject displayObject) {
        displayObject.dispatchEvent(EventPool.createEvent(Event.ADD_TO_STAGE));

        if (displayObject instanceof IDisplayObjectContainer container) {
            for (int i = 0; i < container.getChildCount(); i++) {
                dispatchAddToStage(container.getChild(i));
            }
        }
    }

    static void dispatchRemoveFromStage(@NotNull IDisplayObject displayObject) {
        displayObject.dispatchEvent(EventPool.createEvent(Event.REMOVE_FROM_STAGE));

        if (displayObject instanceof IDisplayObjectContainer container) {
            for (int i = 0; i < container.getChildCount(); i++) {
                dispatchRemoveFromStage(container.getChild(i));
            }
        }
    }
}
