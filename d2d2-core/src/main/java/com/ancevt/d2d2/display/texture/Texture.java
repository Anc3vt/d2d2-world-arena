
package com.ancevt.d2d2.display.texture;

public class Texture {

    private final TextureAtlas textureAtlas;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Texture(TextureAtlas textureAtlas, int x, int y, int width, int height) {
        this.textureAtlas = textureAtlas;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int width() { return width; }
    public int height() { return height; }


    public Texture getSubtexture(int x, int y, int width, int height) {
        return getTextureAtlas().createTexture(x() + x, y() + y, width, height);
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    @Override
    public String toString() {
        return "Texture{" +
                "textureAtlas=" + textureAtlas +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}





















