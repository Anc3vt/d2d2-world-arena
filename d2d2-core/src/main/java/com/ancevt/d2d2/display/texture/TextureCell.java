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
package com.ancevt.d2d2.display.texture;

import com.ancevt.d2d2.display.Color;

public class TextureCell {
	private Color color;

	private boolean pixel;

	private int id;
	private int x;
	private int y;
	private int repeatX = 1;
	private int repeatY = 1;

	private float scaleX = 1.0f;
	private float scaleY = 1.0f;
	private float alpha = 1.0f;
	private float rotation = 0.0f;

	private Texture texture;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isPixel() {
		return pixel;
	}

	public void setPixel(boolean pixel) {
		this.pixel = pixel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getRepeatX() {
		return repeatX;
	}

	public void setRepeatX(int repeatX) {
		this.repeatX = repeatX;
	}

	public int getRepeatY() {
		return repeatY;
	}

	public void setRepeatY(int repeatY) {
		this.repeatY = repeatY;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	@Override
	public String toString() {
		return "TextureCell{" +
				"color=" + color +
				", pixel=" + pixel +
				", id=" + id +
				", x=" + x +
				", y=" + y +
				", repeatX=" + repeatX +
				", repeatY=" + repeatY +
				", scaleX=" + scaleX +
				", scaleY=" + scaleY +
				", alpha=" + alpha +
				", rotation=" + rotation +
				", texture=" + texture +
				'}';
	}
}
