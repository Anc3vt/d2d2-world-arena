
package com.ancevt.d2d2.display;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.display.texture.TextureManager;

public class Sprite extends DisplayObject implements ISprite {

    public static final Color DEFAULT_COLOR = Color.WHITE;

    private int repeatX;
    private int repeatY;
    private Color color;
    private Texture texture;
    private double textureBleedingFix = 0.0005f;
    private ShaderProgram shaderProgram;
    private double vertexBleedingFix;

    public Sprite() {
        setColor(DEFAULT_COLOR);
        setRepeat(1, 1);
    }

    public Sprite(String textureKey) {
        this(D2D2.getTextureManager().getTexture(textureKey));
    }

    public Sprite(Texture texture) {
        setTexture(texture);

        setColor(DEFAULT_COLOR);
        setRepeat(1, 1);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setColor(int rgb) {
        this.color = new Color(rgb);
    }

    @Override
    public Color getColor() {
        return color;
    }


    @Override
    public void setRepeat(int repeatX, int repeatY) {
        setRepeatX(repeatX);
        setRepeatY(repeatY);
    }

    @Override
    public void setRepeatX(int value) {
        this.repeatX = value;
    }

    @Override
    public void setRepeatY(int value) {
        this.repeatY = value;
    }

    @Override
    public int getRepeatX() {
        return repeatX;
    }

    @Override
    public int getRepeatY() {
        return repeatY;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public void setTexture(Texture value) {
        this.texture = value;
        if (texture.getTextureAtlas().isDisposed()) {
            throw new IllegalStateException("Texture atlas " + texture.getTextureAtlas().getId() + " is disposed");
        }
    }

    @Override
    public void setTexture(String textureKey) {
        setTexture(D2D2.getTextureManager().getTexture(textureKey));
    }

    @Override
    public float getWidth() {
        return texture.width();
    }

    @Override
    public float getHeight() {
        return texture.height();
    }

    @Override
    public void onEachFrame() {
        // For overriding
    }

    @Override
    public Sprite cloneSprite() {
        Sprite result = new Sprite(getTexture());
        result.setXY(getX(), getY());
        result.setRepeat(getRepeatX(), getRepeatY());
        result.setScale(getScaleX(), getScaleY());
        result.setAlpha(getAlpha());
        result.setColor(getColor().cloneColor());
        result.setVisible(isVisible());
        result.setRotation(getRotation());
        return result;
    }

    @Override
    public void setTextureBleedingFix(double v) {
        this.textureBleedingFix = v;
    }

    @Override
    public double getTextureBleedingFix() {
        return textureBleedingFix;
    }

    @Override
    public TextureManager textureManager() {
        return D2D2.getTextureManager();
    }

    @Override
    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    @Override
    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    @Override
    public void setVertexBleedingFix(double v) {
        vertexBleedingFix = v;
    }

    @Override
    public double getVertexBleedingFix() {
        return vertexBleedingFix;
    }
}























