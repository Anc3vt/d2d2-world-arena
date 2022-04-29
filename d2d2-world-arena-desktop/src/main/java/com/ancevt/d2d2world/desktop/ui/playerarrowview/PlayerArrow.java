package com.ancevt.d2d2world.desktop.ui.playerarrowview;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.math.RadialUtils;

public class PlayerArrow extends DisplayObjectContainer {

    private final Sprite sprite;
    private final PlayerArrowView playerArrowView;
    private IDisplayObject target;

    private static final float ALPHA_SPEED = 0.05f;
    private float alphaDirection = ALPHA_SPEED;

    public PlayerArrow(PlayerArrowView playerArrowView) {
        this.playerArrowView = playerArrowView;
        sprite = new Sprite(D2D2World.getPlayerArrowTexture());
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
        removeEventListener(PlayerArrow.class);
        addEventListener(PlayerArrow.class, Event.EACH_FRAME, event -> {

            setXY(
                    (target.getX() - playerArrowView.getX() + playerArrowView.getWorld().getX()) / 2 + playerArrowView.getViewportWidth() / 4,
                    (target.getY() - playerArrowView.getY() + playerArrowView.getWorld().getY()) / 2 + playerArrowView.getViewportHeight() / 4
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
                    target.getX() - playerArrowView.getX() + playerArrowView.getWorld().getX() < 0 ||
                    target.getX() - playerArrowView.getX() + playerArrowView.getWorld().getX() > playerArrowView.getViewportWidth() ||
                    target.getY() - playerArrowView.getY() + playerArrowView.getWorld().getY() < 0 ||
                    target.getY() - playerArrowView.getY() + playerArrowView.getWorld().getY() > playerArrowView.getViewportHeight()
            );

            float absoluteX = getAbsoluteX();
            float absoluteY = getAbsoluteY();
            float targetAbsoluteX = target.getAbsoluteX();
            float targetAbsoluteY = target.getAbsoluteY();
            float deg = -RadialUtils.getDegreeBetweenPoints(absoluteX, absoluteY, targetAbsoluteX, targetAbsoluteY);
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
