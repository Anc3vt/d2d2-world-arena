package ru.ancevt.d2d2world.desktop.motion;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.IDisplayObject;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.event.Event;
import ru.ancevt.d2d2.event.EventListener;
import ru.ancevt.d2d2.event.InputEvent;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;

import java.util.HashMap;
import java.util.Map;

public class Motion implements EventListener {

    private static final Map<IDisplayObject, Motion> motions = new HashMap<>();

    private static float defaultSmoothFactor = 25; // changeable

    private final IDisplayObject displayObject;
    private final float targetX;
    private final float targetY;
    private final float smoothFactor;

    private Motion(IDisplayObject displayObject, float targetX, float targetY, float smoothFactor) {
        this.displayObject = displayObject;
        this.targetX = targetX;
        this.targetY = targetY;
        this.smoothFactor = smoothFactor;
    }

    @Override
    public void onEvent(Event event) { // EACH_FRAME
        float x = displayObject.getX();
        float y = displayObject.getY();

        float diffX = targetX - x;
        float diffY = targetY - y;

        displayObject.move(diffX / smoothFactor, diffY / smoothFactor);

        if (Math.abs(x - targetX) < 2 && Math.abs(y - targetY) < 2) {
            stopMotion(displayObject);
            displayObject.setXY(targetX, targetY);
        }
    }

    @Override
    public String toString() {
        return "Motion{" +
                "displayObject=" + displayObject +
                ", targetX=" + targetX +
                ", targetY=" + targetY +
                ", smooth=" + smoothFactor +
                '}';
    }

    public static void setDefaultSmoothFactor(float defaultSmoothFactor) {
        Motion.defaultSmoothFactor = defaultSmoothFactor;
    }

    public static float getDefaultSmoothFactor() {
        return defaultSmoothFactor;
    }

    public static void moveTo(IDisplayObject displayObject, float moveToX, float moveToY) {
        moveTo(displayObject, moveToX, moveToY, defaultSmoothFactor);
    }

    public static void moveTo(IDisplayObject displayObject, float moveToX, float moveToY, float smoothFactor) {
        stopMotion(displayObject);
        Motion motion = new Motion(displayObject, moveToX, moveToY, smoothFactor);
        motions.put(displayObject, motion);
        displayObject.addEventListener(Event.EACH_FRAME, motion);
    }

    public static void stopMotion(IDisplayObject displayObject) {
        Motion oldMotion = motions.remove(displayObject);
        if (oldMotion != null) {
            oldMotion.displayObject.removeEventListener(Event.EACH_FRAME, oldMotion);
        }
    }

    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        Root root = D2D2.getStage().getRoot();

        Sprite sprite = new Sprite("satellite");

        root.add(sprite);

        Motion.moveTo(sprite, 300, 100);

        root.addEventListener(InputEvent.MOUSE_DOWN, e -> {
            if (e instanceof InputEvent ie) {
                Motion.moveTo(sprite, ie.getX(), ie.getY(), 10);
            }
        });

        D2D2.loop();
    }
}



























