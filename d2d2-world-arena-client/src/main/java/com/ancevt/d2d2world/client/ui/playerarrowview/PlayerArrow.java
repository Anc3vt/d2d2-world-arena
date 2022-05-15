
package com.ancevt.d2d2world.client.ui.playerarrowview;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.client.D2D2WorldArenaDesktopAssets;
import com.ancevt.d2d2world.math.RadialUtils;

public class PlayerArrow extends DisplayObjectContainer {

    private final Sprite sprite;
    private final PlayerArrowView playerArrowView;
    private IDisplayObject target;

    private static final float ALPHA_SPEED = 0.05f;
    private float alphaDirection = ALPHA_SPEED;

    public PlayerArrow(PlayerArrowView playerArrowView) {
        this.playerArrowView = playerArrowView;
        sprite = new Sprite(D2D2WorldArenaDesktopAssets.getPlayerArrowTexture());
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
