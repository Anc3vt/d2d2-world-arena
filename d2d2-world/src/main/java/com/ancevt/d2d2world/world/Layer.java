
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.display.DisplayObjectContainer;

public class Layer extends DisplayObjectContainer {

    public final static int LAYER_COUNT = 10;
    private final int index;

    public Layer(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Layer{" +
                "index=" + index +
                '}';
    }
}
