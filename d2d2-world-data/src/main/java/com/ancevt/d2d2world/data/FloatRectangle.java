
package com.ancevt.d2d2world.data;

public final class FloatRectangle extends Rectangle<Float>{

    public FloatRectangle() {
        setX(0f);
        setY(0f);
        setWidth(0f);
        setHeight(0f);
    }

    public FloatRectangle(String source) {
        String[] split = source.split(DELIMITER);
        setX(Float.parseFloat(split[0]));
        setY(Float.parseFloat(split[1]));
        setWidth(Float.parseFloat(split[2]));
        setHeight(Float.parseFloat(split[3]));
    }

    @Override
    public String toString() {
        return stringify();
    }
}
