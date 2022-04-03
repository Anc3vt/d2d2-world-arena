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

public record Texture(TextureAtlas textureAtlas, int x, int y, int width, int height) {

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





















