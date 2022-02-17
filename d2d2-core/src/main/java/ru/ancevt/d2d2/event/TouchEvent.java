package ru.ancevt.d2d2.event;

public class TouchEvent extends Event {

    public static final String TOUCH_DOWN = "touchDown";
    public static final String TOUCH_UP = "touchUp";
    public static final String TOUCH_DRAG = "touchDrag";
    public static final String TOUCH_HOVER = "touchHover";

    private final int x;
    private final int y;
    private final boolean onArea;

    public TouchEvent(String type, IEventDispatcher source, int x, int y, boolean onArea) {
        super(type, source);
        this.x = x;
        this.y = y;
        this.onArea = onArea;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOnArea() {
        return onArea;
    }
}
