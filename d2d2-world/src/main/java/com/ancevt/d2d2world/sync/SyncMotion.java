package com.ancevt.d2d2world.sync;

import com.ancevt.commons.Pair;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncMotion {

    private static final Map<IDisplayObject, Pair<Float, Float>> map = new ConcurrentHashMap<>();

    public static void moveMotion(IDisplayObject o, float x, float y) {
        //var targetXY = map.get(o);
        map.put(o, Pair.of(x, y));

        o.removeEventListeners(SyncMotion.class);
        o.addEventListener(SyncMotion.class, Event.REMOVE_FROM_STAGE, event -> {
            map.remove(o);
            o.removeEventListeners(SyncMotion.class);
        });
    }

    public static void clear() {
        map.clear();
    }

    public static void process() {
        List<IDisplayObject> toRemove = new LinkedList<>();

        map.forEach((displayObject, targetXY) -> {
            float oX = displayObject.getX();
            float oY = displayObject.getY();

            float tX = targetXY.getFirst();
            float tY = targetXY.getSecond();

            final float factor = 5f;

            float speedX = (tX - oX) / factor;
            float speedY = (tY - oY);

            displayObject.move(speedX, speedY);

            if (Math.abs(tX - oX) < 1f && Math.abs(tY - oY) < 1f) {
                toRemove.add(displayObject);
            }
        });

        toRemove.forEach(map::remove);
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLStarter(800, 600, "(floating"));

        Sprite sprite = new Sprite("satellite");

        root.add(sprite);

        root.addEventListener(InputEvent.MOUSE_DOWN, event -> {
            if (event instanceof InputEvent e) {
                SyncMotion.moveMotion(sprite, e.getX(), e.getY());
            }
        });

        root.addEventListener(Event.EACH_FRAME, event -> SyncMotion.process());

        D2D2.loop();
    }
}
