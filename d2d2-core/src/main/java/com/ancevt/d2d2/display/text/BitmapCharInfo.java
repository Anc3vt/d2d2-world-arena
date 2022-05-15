
package com.ancevt.d2d2.display.text;

public class BitmapCharInfo {

    private final char character;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public BitmapCharInfo(char character, int x, int y, int width, int height) {
        this.character = character;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public char character() {
        return character;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
