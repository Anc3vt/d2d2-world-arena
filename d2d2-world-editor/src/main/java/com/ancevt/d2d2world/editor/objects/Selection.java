/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2world.editor.objects;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2world.gameobject.ICollision;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.IRepeatable;
import com.ancevt.d2d2world.gameobject.ISizable;

public class Selection extends Container {
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

    public static class RepeatControl extends Container {

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


