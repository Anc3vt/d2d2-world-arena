/*
 *   D2D2 World Arena Desktop
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
package ru.ancevt.d2d2world.game.ui;

import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.IDisplayObject;
import ru.ancevt.d2d2.display.text.BitmapFont;
import ru.ancevt.d2d2.display.text.BitmapText;

public class UiText extends DisplayObjectContainer {

    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 30.0f;

    private IDisplayObject fore;
    private IDisplayObject shadow;

    private String text;
    private BitmapText bitmapText;
    private BitmapText bitmapTextShadow;
    private float width;
    private float height;
    private boolean shadowEnabled;
    private Color color;

    public UiText() {
        color = DEFAULT_COLOR;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        shadowEnabled = true;
        redraw();
    }

    public void setText(String text) {
        this.text = text;
        redraw();
    }

    public String getText() {
        return text;
    }

    public boolean isShadowEnabled() {
        return shadowEnabled;
    }

    public void setShadowEnabled(boolean shadowEnabled) {
        if (this.shadowEnabled == shadowEnabled) return;
        this.shadowEnabled = shadowEnabled;
        redraw();
    }

    public void append(String string) {
        setText(getText().concat(string));
    }

    public void setWidth(float width) {
        this.width = width;
        redraw();
    }

    public void setHeight(float height) {
        this.height = height;
        redraw();
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        redraw();
    }

    public float getTextWidth() {
        return (getCharWidth() + bitmapText.getSpacing()) * getText().length();
    }

    public float getCharWidth() {
        return getBitmapFont().getCharInfo('0').width();
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setColor(Color color) {
        if (this.color.equals(color)) return;
        this.color = color;
        redraw();
    }

    public Color getColor() {
        return color;
    }

    public void redraw() {
        if (shadow != null) shadow.removeFromParent();

        if (shadowEnabled) {
            bitmapTextShadow = new BitmapText(Font.getBitmapFont());
            bitmapTextShadow.setBounds(getWidth(), getHeight());
            bitmapTextShadow.setColor(Color.BLACK);
            bitmapTextShadow.setText(text);
            shadow = bitmapTextShadow;//.toSprite();
            add(shadow, 0, 0);
        }

        if (fore != null) fore.removeFromParent();

        bitmapText = new BitmapText(Font.getBitmapFont());
        bitmapText.setBounds(getWidth(), getHeight());
        bitmapText.setColor(getColor());
        bitmapText.setText(text);

        fore = bitmapText;//.toSprite();
        shadow = bitmapTextShadow;//.toSprite();

        add(fore, 1, 0);
    }

    public BitmapFont getBitmapFont() {
        return Font.getBitmapFont();
    }
}
