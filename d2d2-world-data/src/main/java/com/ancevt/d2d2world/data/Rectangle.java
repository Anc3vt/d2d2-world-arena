
package com.ancevt.d2d2world.data;

public abstract class Rectangle<T extends Number> {

    protected static final String DELIMITER = ",";

    private T x;

    private T y;

    private T width;

    private T height;

    public T getX() {
        return x;
    }

    void setX(T x) {
        this.x = x;
    }

    public T getY() {
        return y;
    }

    void setY(T y) {
        this.y = y;
    }

    public T getWidth() {
        return width;
    }

    void setWidth(T width) {
        this.width = width;
    }

    public T getHeight() {
        return height;
    }

    void setHeight(T height) {
        this.height = height;
    }

    public String stringify() {
        return x + "," + y + "," + width + "," + height;
    }

}
