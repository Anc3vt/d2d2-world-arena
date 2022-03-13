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

import com.ancevt.d2d2.display.text.BitmapText;

import java.io.InputStream;

public interface ITextureEngine {

    boolean bind(TextureAtlas textureAtlas);

    void enable(TextureAtlas textureAtlas);

    void disable(TextureAtlas textureAtlas);

    TextureAtlas createTextureAtlas(InputStream pngInputStream);

    TextureAtlas createTextureAtlas(String assetPath);

    TextureAtlas createTextureAtlas(int width, int height, TextureCell[] cells);

    void unloadTextureAtlas(TextureAtlas textureAtlas);

    TextureAtlas bitmapTextToTextureAtlas(BitmapText bitmapText);

}
