/*
 *   D2D2 core
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
package com.ancevt.d2d2.display.text;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.IColored;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.TextureManager;

public class BitmapText extends DisplayObject implements IColored {
	
	protected static final String DEFAULT_TEXT = "";

	protected static final float DEFAULT_BOUND_WIDTH = 512f;
	protected static final float DEFAULT_BOUND_HEIGHT = 512f;

	protected static final Color DEFAULT_COLOR = Color.WHITE;
	
	private String text;
	private Color color;
	
	private BitmapFont bitmapFont;
	
	private float lineSpacing;
	private float spacing;
	
	private float boundWidth;
	private float boundHeight;

	private double vertexBleedingFix = 0.0;
	
	public BitmapText(final BitmapFont bitmapFont, float boundWidth, float boundHeight, String text) {
		setBitmapFont(bitmapFont);
		setColor(DEFAULT_COLOR);
		setBoundWidth(boundWidth);
		setBoundHeight(boundHeight);
		setText(text);
	}

	public BitmapText(final BitmapFont bitmapFont, float boundWidth, float boundHeight) {
		this(bitmapFont, boundWidth, boundHeight, DEFAULT_TEXT);
	}

	public BitmapText(String text) {
		this(BitmapFont.getDefaultBitmapFont(), DEFAULT_BOUND_WIDTH, DEFAULT_BOUND_HEIGHT, text);
	}

	public BitmapText(final BitmapFont bitmapFont) {
		this(bitmapFont, DEFAULT_BOUND_WIDTH, DEFAULT_BOUND_HEIGHT, DEFAULT_TEXT);
	}
	
	public BitmapText(float boundWidth, float boundHeight) {
		this(BitmapFont.getDefaultBitmapFont(), boundWidth, boundHeight, DEFAULT_TEXT);
	}
	
	public BitmapText() {
		this(BitmapFont.getDefaultBitmapFont(), DEFAULT_BOUND_WIDTH, DEFAULT_BOUND_HEIGHT, DEFAULT_TEXT);
	}

	public void setVertexBleedingFix(double vertexBleedingFix) {
		this.vertexBleedingFix = vertexBleedingFix;
	}

	public double getVertexBleedingFix() {
		return vertexBleedingFix;
	}

	public int getTextWidth() {
		if(getText() == null) return 0;
		
		final char[] chars = getText().toCharArray();
		int result = 0;
		
		final BitmapFont font = getBitmapFont();
		
		int max = 0;

		for (final char c : chars) {
			if (c == '\n' || (getBoundWidth() > 0 && result > getBoundWidth())) result = 0;

			BitmapCharInfo info = font.getCharInfo(c);
			if (info == null) continue;

			result += info.width() + getSpacing();

			if (result > max) max = result;
		}
		
		return (int)(max - getSpacing());
	}
	
	public int getTextHeight() {
		if(getText() == null) return 0;
		
		final char[] chars = getText().toCharArray();
		int result = 0;
		
		final BitmapFont font = getBitmapFont();

		for (final char c : chars) {
			if (c == '\n' || (getBoundWidth() > 0 && result > getBoundWidth())) {
				result += font.getCharHeight() + getLineSpacing();
			}
		}
		
		return result + font.getCharHeight();
	}

	public Sprite toSprite() {
		Sprite result = new Sprite(D2D2.getTextureManager().bitmapTextToTextureAtlas(this).createTexture());
		result.setXY(getX(), getY());
		result.setScale(getScaleX(), getScaleY());
		result.setRotation(getRotation());
		result.setColor(getColor());
		return result;
	}
	
	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void setColor(int rgb) {
		setColor(new Color(rgb));
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public TextureManager textureManager() {
		return D2D2.getTextureManager();
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public boolean isEmpty() {
		return text == null || text.length() == 0;
	}

	public BitmapFont getBitmapFont() {
		return bitmapFont;
	}

	public void setBitmapFont(BitmapFont bitmapFont) {
		this.bitmapFont = bitmapFont;
	}

	public void setLineSpacing(float value) {
		this.lineSpacing = value;
	}

	public float getLineSpacing() {
		return lineSpacing;
	}

	public void setSpacing(float value) {
		this.spacing = value;
	}

	public float getSpacing() {
		return spacing;
	}

	public float getBoundWidth() {
		return boundWidth;
	}

	public float getBoundHeight() {
		return boundHeight;
	}

	public void setBoundWidth(float value) {
		boundWidth = value;
	}

	public void setBoundHeight(float value) {
		boundHeight = value;
	}
	
	public float getWidth() {
		return getBoundWidth();
	}
	
	public float getHeight() {
		return getBoundHeight();	
	}
	
	public void setBounds(float boundWidth, float boundHeight) {
		setBoundWidth(boundWidth);
		setBoundHeight(boundHeight);
	}

	@Override
	public void onEachFrame() {
		// For overriding
	}

	@Override
	public String toString() {
		return "BitmapText{" +
				"text='" + text + '\'' +
				", color=" + color +
				", bitmapFont=" + bitmapFont +
				", lineSpacing=" + lineSpacing +
				", spacing=" + spacing +
				", boundWidth=" + boundWidth +
				", boundHeight=" + boundHeight +
				'}';
	}
}

















