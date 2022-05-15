
package com.ancevt.d2d2world.editor.objects;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.IRepeatable;
import com.ancevt.d2d2world.gameobject.ISizable;

public class Selection extends DisplayObjectContainer {
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

        if (gameObject instanceof IRepeatable || gameObject instanceof ISizable) {
            repeatControl = new RepeatControl();
            add(repeatControl, getWidth() - 8, getHeight() - 8);
        }
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
    public void onEachFrame() {
        super.onEachFrame();
        setXY(gameObject.getX(), gameObject.getY());

        if (gameObject instanceof ICollision c) {
            move(c.getCollisionX(), c.getCollisionY());
        }

        setSize(gameObject.getWidth(), gameObject.getHeight());

        if (repeatControl != null && repeatControl.hasParent()) {
            repeatControl.setXY(getWidth() - 8, getHeight() - 8);
        }
    }

    public static class RepeatControl extends DisplayObjectContainer {

        public RepeatControl() {
            PlainRect shadow = new PlainRect(10, 10);
            shadow.setColor(Color.BLACK);

            add(shadow, -1, -1);

            PlainRect rect = new PlainRect(8, 8);
            rect.setColor(Color.WHITE);
            add(rect);

        }

    }
}


