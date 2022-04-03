package com.ancevt.d2d2world.fx;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;

import java.util.concurrent.TimeUnit;

public class Ring extends DisplayObjectContainer {

    private final Sprite sprite;

    public Ring() {
        sprite = new Sprite("d2d2-world-common-tileset-ring");
        add(sprite, -sprite.getWidth() / 2, -sprite.getHeight() / 2);
    }

    public void setColor(Color color) {
        sprite.setColor(color);
    }

    public Color getColor() {
        return sprite.getColor();
    }

    public static void ringEffect(IDisplayObject target, int count, Color color) {
        if (!target.hasParent()) return;

        for (int i = 0; i < count; i++) {
            int finalI = i;

            Async.runLater(100L * i, TimeUnit.MILLISECONDS, () -> {
                Ring ring = new Ring() {
                    @Override
                    public void onEachFrame() {
                        setXY(target.getX(), target.getY());
                        toScale(0.95f, 0.95f);
                        toAlpha(0.95f);

                        if (getScaleX() <= 0.05f) {
                            removeFromParent();
                        }
                    }
                };

                ring.setRotation(10f * finalI);
                ring.setScale(finalI / 2f, finalI / 2f);
                ring.setColor(color);
                target.getParent().add(ring, target.getX(), target.getY());
            });
        }
    }
}
