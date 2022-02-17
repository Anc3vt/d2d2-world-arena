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
package ru.ancevt.d2d2.common;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.display.Color;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2.display.texture.TextureAtlas;
import ru.ancevt.d2d2.display.texture.TextureManager;

public class PlainRect extends Sprite {
	
	private static final String FILE_PATH = "1x1.png";

	private static Texture texture;

	private static Texture get1x1Texture() {
		if(texture != null) return texture;
		
		final TextureManager textureManager = D2D2.getTextureManager();
		final TextureAtlas textureAtlas = textureManager.loadTextureAtlas(FILE_PATH);
		return texture = textureAtlas.createTexture();
	}
	
	public PlainRect() {
		super(get1x1Texture());
	}
	
	public PlainRect(float width, float height) {
		this();
		setSize(width, height);
	}
	
	public PlainRect(Color color) {
		this();
		setColor(color);
	}
	
	public PlainRect(float width, float height, Color color) {
		this();
		setSize(width, height);
		setColor(color);
	}
	
	public void setWidth(float width) {
		setScaleX(width);
	}
	
	public void setHeight(float height) {
		setScaleY(height);
	}
	
	public void setSize(float width, float height) {
		setWidth(width);
		setHeight(height);
	}
	
	@Override
	public float getWidth() {
		return getScaleX();
	}
	
	@Override
	public float getHeight() {
		return getScaleY();
	}

}
