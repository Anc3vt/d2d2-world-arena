
package com.ancevt.d2d2.common;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.display.texture.TextureAtlas;
import com.ancevt.d2d2.display.texture.TextureManager;

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
