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
package com.ancevt.d2d2world.client.ui.playerarrowview;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;
import com.ancevt.d2d2world.math.RadialUtils;

public class PlayerArrow extends Container {

    private final Sprite sprite;
    private final PlayerArrowView playerArrowView;
    private IDisplayObject target;

    private static final float ALPHA_SPEED = 0.05f;
    private float alphaDirection = ALPHA_SPEED;

    public PlayerArrow(PlayerArrowView playerArrowView) {
        this.playerArrowView = playerArrowView;
        sprite = new Sprite(D2D2WorldArenaClientAssets.getPlayerArrowTexture());
        add(sprite, -sprite.getWidth() - 2, -sprite.getHeight() / 2);
    }

    public void setColor(Color color) {
        sprite.setColor(color);
    }

    public Color getColor() {
        return sprite.getColor();
    }

    public void setTarget(IDisplayObject displayObject) {
        if (this.target == displayObject) return;

        this.target = displayObject;
        removeEventListener(this, Event.EACH_FRAME);
        addEventListener(this, Event.EACH_FRAME, event -> {
            IDisplayObject from = playerArrowView.getFrom();

            float tax = target.getAbsoluteX() / target.getAbsoluteScaleX();
            float tay = target.getAbsoluteY() / target.getAbsoluteScaleY();

            setXY(
                    tax / 2 + playerArrowView.getViewportWidth() / 4,
                    tay / 2 + playerArrowView.getViewportHeight() / 4
            );

            if (getX() < 0) {
                setX(0);
            } else if (getX() > playerArrowView.getViewportWidth()) {
                setX(playerArrowView.getViewportWidth());
            }

            if (getY() < 0) {
                setY(0);
            } else if (getY() > playerArrowView.getViewportHeight()) {
                setY(playerArrowView.getViewportHeight());
            }


            sprite.setVisible(
                    tax < 0 ||
                    tax > playerArrowView.getViewportWidth() ||
                    tay < 0 ||
                    tay > playerArrowView.getViewportHeight()
            );

            if (playerArrowView.getFrom() == null) {
                return;
            }

            float fromAbsoluteX = from.getAbsoluteX() / from.getAbsoluteScaleX();
            float fromAbsoluteY = from.getAbsoluteY() / from.getAbsoluteScaleY();
            float targetAbsoluteX = target.getAbsoluteX() / target.getAbsoluteScaleX();
            float targetAbsoluteY = target.getAbsoluteY() / target.getAbsoluteScaleY();
            float deg = -RadialUtils.getDegreeBetweenPoints(fromAbsoluteX, fromAbsoluteY, targetAbsoluteX, targetAbsoluteY);
            setRotation(deg);

            setAlpha(getAlpha() + alphaDirection);
            if (getAlpha() >= 1.0f) alphaDirection = -ALPHA_SPEED;
            else if (getAlpha() <= 0.0f) alphaDirection = ALPHA_SPEED;
        });
    }

    public IDisplayObject getTarget() {
        return target;
    }

}
