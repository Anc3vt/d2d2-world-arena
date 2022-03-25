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
package com.ancevt.d2d2.display.texture;

import java.util.ArrayList;
import java.util.List;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;

public class TextureCombiner {
	private final List<TextureCell> cells;
	private final int width;
	private final int height;
	private int cellIdCounter;
	
	public TextureCombiner(final int width, final int height) {
		this.width = width;
		this.height = height;
	
		cells = new ArrayList<>();
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public final int append(
			final Texture texture,
			final int x,
			final int y,
			final float scaleX,
			final float scaleY,
			final float alpha,
			final float rotation,
			final int repeatX,
			final int repeatY) {
		
		final TextureCell cell = new TextureCell();
		cell.setX(x);
		cell.setY(y);
		cell.setTexture(texture);
		cell.setScaleX(scaleX);
		cell.setScaleY(scaleY);
		cell.setAlpha(alpha);
		cell.setRotation(rotation);
		cell.setRepeatX(repeatX);
		cell.setRepeatY(repeatY);
		cell.setId(cellIdCounter++);
		cells.add(cell);
		
		return cell.getId();
	}
	
	public final int append(int x, int y, Color color, float alpha) {
		final TextureCell cell = new TextureCell();
		cell.setAlpha(alpha);
		cell.setPixel(true);
		cell.setColor(color);
		cell.setX(x);
		cell.setY(y);
		cell.setId(cellIdCounter++);
		cells.add(cell);
		
		return cell.getId();
	}
	
	public final int append(int x, int y, Color color) {
		final TextureCell cell = new TextureCell();
		cell.setPixel(true);
		cell.setColor(color);
		cell.setX(x);
		cell.setY(y);
		cell.setId(cellIdCounter++);
		cells.add(cell);
		return cell.getId();
	}
	
	public final int append(
			final Texture texture,
			final int x,
			final int y,
			final float scaleX,
			final float scaleY) {
		final TextureCell cell = new TextureCell();
		cell.setX(x);
		cell.setY(y);
		cell.setTexture(texture);
		cell.setScaleX(scaleX);
		cell.setScaleY(scaleY);
		cell.setId(cellIdCounter++);
		cells.add(cell);
		return cell.getId();
	}
	
	public final int append(
			final Texture texture,
			final int x,
			final int y,
			final int repeatX,
			final int repeatY) {
		final TextureCell cell = new TextureCell();
		cell.setX(x);
		cell.setY(y);
		cell.setTexture(texture);
		cell.setRepeatX(repeatX);
		cell.setRepeatY(repeatY);
		cell.setId(cellIdCounter++);
		cells.add(cell);
		
		return cell.getId();
	}

	public final int append(
			final Texture texture,
			final int x,
			final int y) {
		final TextureCell cell = new TextureCell();
		cell.setX(x);
		cell.setY(y);
		cell.setTexture(texture);
		cell.setId(cellIdCounter++);
		cells.add(cell);
		
		return cell.getId();
	}
	
	public final void remove(final int cellId) {
		final int count = cells.size();
		for(int i = 0; i < count; i ++) {
			final TextureCell cell = cells.get(i);
			if(cell.getId() == cellId) {
				cells.remove(cell);
				return;
			}
		}
	}
	
	public final TextureAtlas createTextureAtlas() {
		return D2D2.getTextureManager().getTextureEngine().
			createTextureAtlas(width, height, cells.toArray(new TextureCell[] {}));
	}
	
	public static TextureAtlas bitmapTextToTextureAtlas(final BitmapText bitmapText) {
		return D2D2.getTextureManager().bitmapTextToTextureAtlas(bitmapText);
	}
	
	public static Texture bitmapTextToTexture(final BitmapText bitmapText) {
		final TextureAtlas textureAtlas = bitmapTextToTextureAtlas(bitmapText);
		if(textureAtlas != null) {
			return textureAtlas.createTexture();
		}
		return null;
	}
}




























