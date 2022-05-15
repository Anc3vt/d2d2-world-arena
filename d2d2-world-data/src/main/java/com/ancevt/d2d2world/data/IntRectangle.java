
package com.ancevt.d2d2world.data;

import com.ancevt.d2d2.display.texture.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public final class IntRectangle extends Rectangle<Integer> {

    public IntRectangle() {
        setX(0);
        setY(0);
        setWidth(0);
        setHeight(0);
    }

    public IntRectangle(@NotNull String source) {
        String[] split = source.split(DELIMITER);
        setX(Integer.parseInt(split[0]));
        setY(Integer.parseInt(split[1]));
        setWidth(Integer.parseInt(split[2]));
        setHeight(Integer.parseInt(split[3]));
    }

    public static IntRectangle @NotNull [] getIntRectangles(@NotNull String source) {
        source = TextureAtlas.convertCoords(source);
        String[] split = source.split(";");
        IntRectangle[] result = new IntRectangle[split.length];
        for(int i = 0; i < result.length; i ++) {
            split[i] = split[i].trim();

            result[i] = new IntRectangle(split[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        return stringify();
    }
}
