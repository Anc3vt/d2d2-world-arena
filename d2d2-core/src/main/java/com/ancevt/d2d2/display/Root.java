
package com.ancevt.d2d2.display;

public class Root extends DisplayObjectContainer {

    private static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;

    private Color backgroundColor;

    public Root() {
        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
