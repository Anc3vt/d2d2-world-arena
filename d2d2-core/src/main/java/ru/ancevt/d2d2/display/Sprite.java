/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ru.ancevt.d2d2.display;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2.display.texture.TextureManager;

public class Sprite extends DisplayObject implements ISprite {

    public static final Color DEFAULT_COLOR = Color.WHITE;

    private int repeatX;
    private int repeatY;
    private Color color;
    private Texture texture;

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
    public TextureManager textureManager() {
        return D2D2.getTextureManager();
    }
}























