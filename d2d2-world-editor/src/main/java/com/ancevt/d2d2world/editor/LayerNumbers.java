
package com.ancevt.d2d2world.editor;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.common.PlainRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.world.Layer;
import com.ancevt.d2d2world.world.World;

import java.util.ArrayList;
import java.util.List;

public class LayerNumbers {

    private static final List<Label> labels = new ArrayList<>();

    private static boolean onScreen;

    public static void show(World world) {
        onScreen = true;

        for (int i = 0; i < world.getGameObjectCount(); i++) {
            IGameObject gameObject = world.getGameObject(i);

            if (gameObject.getParent() instanceof Layer layer) {
                String text = String.valueOf(layer.getIndex());

                Label label = new Label(world, i, text, gameObject);

                label.addEventListener(Event.EACH_FRAME, e -> checkLabelHitTests(label));

                world.add(label, gameObject.getX(), gameObject.getY());

                labels.add(label);
            }
        }
    }

    public static boolean isShow() {
        return onScreen;
    }

    private static void checkLabelHitTests(Label label) {
        labels.forEach(current -> {
            if (current != label) {
                if (hitTest(current, label) && current.index > label.index) {
                    spread(current, label);
                }
            }
        });
    }

    private static boolean hitTest(Label l1, Label l2) {
        return l1.getX() >= l2.getX() && l1.getX() < l2.getX() + l2.getWidth() &&
                l1.getY() >= l2.getY() && l1.getY() < l2.getY() + l2.getHeight();
    }

    private static void spread(Label l1, Label l2) {

        int factor = (l2.index - l1.index) > 0 ? 1 : -1;

        l1.move((float) (factor * (Math.random() * 30)), factor * 8);
        l1.linesToStartPosition();
    }

    public static void hide() {
        onScreen = false;
        while (!labels.isEmpty()) {
            Label label = labels.remove(0);
            label.clear();
            label.removeFromParent();
        }
    }

    private static class Label extends DisplayObjectContainer {

        private final World world;
        private final int index;
        private final BitmapText fore;
        private final BitmapText back;

        private final float startX;
        private final float startY;
        private final IGameObject gameObject;

        private PlainRect hor;
        private PlainRect vert;

        private BorderedRect borderedRect;

        public Label(World world, int index, String text, IGameObject gameObject) {
            this.world = world;
            this.index = index;
            fore = new BitmapText(text);
            back = new BitmapText(text);

            startX = gameObject.getX();
            startY = gameObject.getY();
            this.gameObject = gameObject;

            back.setColor(Color.BLACK);
            fore.setColor(Color.createRandomColor());

            borderedRect = new BorderedRect(
                    gameObject.getWidth() * gameObject.getScaleX(),
                    gameObject.getHeight() * gameObject.getScaleY(),
                    null, fore.getColor()
            );

            world.add(borderedRect, gameObject.getX(), gameObject.getY());

            add(back, 2, -1);
            add(fore, 1, -2);
        }

        @Override
        public float getWidth() {
            return 16;
        }

        @Override
        public float getHeight() {
            return 16;
        }

        public void clear() {
            if (hor != null && vert != null) {
                hor.removeFromParent();
                vert.removeFromParent();
            }
            borderedRect.removeFromParent();
        }

        public void linesToStartPosition() {
            clear();

            world.add(borderedRect, gameObject.getX(), gameObject.getY());

            hor = new PlainRect(Math.abs(getX() - startX), 1f, fore.getColor());
            vert = new PlainRect(1f, Math.abs(getY() - startY), fore.getColor());

            float offset = 5;

            hor.setXY(getX(), startY);
            vert.setXY(getX(), getY());

            getParent().add(hor);
            getParent().add(vert);

            fore.setXY(-2, -14);
            back.setXY(fore.getX() + 1, fore.getY() + 1);
        }
    }
}



























