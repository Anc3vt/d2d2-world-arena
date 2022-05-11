/*
 *   D2D2 World Arena Client
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.text.BitmapFont;
import com.ancevt.d2d2.display.text.BitmapText;

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
    private boolean autoSize;
    private double vertexBleedingFix;

    public UiText() {
        color = DEFAULT_COLOR;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        shadowEnabled = true;
        text = "";
        //redraw();
    }

    public UiText(Object text) {
        this();
        setText(text);
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
        redraw();
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    public void setText(Object text) {
        this.text = String.valueOf(text);
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
        return getCharWidth() + (getCharWidth() * text.length());
    }

    public float getCharWidth() {
        return getBitmapFont().getCharInfo('0').width();
    }

    private float getCharHeight() {
        return getBitmapFont().getCharInfo('0').height();
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

    public void setVertexBleedingFix(double v) {
        vertexBleedingFix = v;
        if (bitmapTextShadow != null) bitmapTextShadow.setVertexBleedingFix(vertexBleedingFix);
        if (bitmapText != null) bitmapTextShadow.setVertexBleedingFix(vertexBleedingFix);
    }

    public double getVertexBleedingFix() {
        return vertexBleedingFix;
    }

    public void redraw() {
        if (shadow != null) {
            shadow.removeFromParent();
        }

        if (shadowEnabled) {
            bitmapTextShadow = new BitmapText(Font.getBitmapFont());
            bitmapTextShadow.setBounds(getWidth(), getHeight());
            bitmapTextShadow.setColor(Color.BLACK);
            bitmapTextShadow.setVertexBleedingFix(vertexBleedingFix);
            bitmapTextShadow.setText(text);

            if (autoSize) {
                bitmapTextShadow.setBounds(getTextWidth(), getCharHeight());
            }

            shadow = bitmapTextShadow;//.toSprite();
            add(shadow, 0, -1);
        }

        if (fore != null) fore.removeFromParent();

        bitmapText = new BitmapText(Font.getBitmapFont());
        bitmapText.setBounds(getWidth(), getHeight());
        bitmapText.setColor(getColor());
        bitmapText.setVertexBleedingFix(vertexBleedingFix);
        bitmapText.setText(text);

        if (autoSize) {
            bitmapText.setBounds(getTextWidth(), getCharHeight());
        }

        fore = bitmapText;//.toSprite();

        add(fore, 1, 0);
    }

    public BitmapFont getBitmapFont() {
        return Font.getBitmapFont();
    }
}
