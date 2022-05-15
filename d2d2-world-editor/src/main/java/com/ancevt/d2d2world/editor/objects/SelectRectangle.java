
package com.ancevt.d2d2world.editor.objects;

public class SelectRectangle {
    private float x1, y1, x2, y2;
    private boolean pressed;

    public float getY2() {
        return Math.max(y2, y1);
    }

    public void setY2(float height) {
        this.y2 = height;
    }

    public float getX2() {
        return Math.max(x2, x1);
    }

    public void setX2(float width) {
        this.x2 = width;
    }

    public float getY1() {
        return Math.min(y2, y1);
    }

    public void setY1(float y) {
        this.y1 = y;
    }

    public float getX1() {
        return Math.min(x2, x1);
    }

    public void setX1(float x) {
        this.x1 = x;
    }

    public float getWidth() {
        return getX2() - getX1();
    }

    public float getHeight() {
        return getY2() - getY1();
    }

    public void setUp(float x1, float y1, float x2, float y2) {
        setX1(x1);
        setY1(y1);
        setX2(x2);
        setY2(y2);
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public String toString() {
        return "SelectRectangle{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", pressed=" + pressed +
                '}';
    }
}
