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

import java.util.Arrays;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class TextureAtlas {

    private final int id;

    private int width;
    private int height;

    private boolean disposed;

    private TextureAtlas(int id) {
        this.id = id;
    }

    public TextureAtlas(int id, int width, int height) {
        this(id);
        setUp(width, height);
    }

    final void setUp(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Texture createTexture() {
        return createTexture(0, 0, getWidth(), getHeight());
    }

    public Texture createTexture(int x, int y, int width, int height) {
        return new Texture(this, x, y, width, height);
    }

    /**
     * 16,16,48,48
     *
     * @param textureCoords
     * @return
     */
    public Texture createTexture(String textureCoords) {
        StringTokenizer stringTokenizer = new StringTokenizer(textureCoords, ",");
        return new Texture(this,
                parseInt(stringTokenizer.nextToken().trim()),
                parseInt(stringTokenizer.nextToken().trim()),
                parseInt(stringTokenizer.nextToken().trim()),
                parseInt(stringTokenizer.nextToken().trim())
        );
    }

    /**
     * 16,16,48,48; 16,16,48,48; 16,16,48,48
     * @param textureCoords
     * @return
     */
    public Texture[] createTextures(String textureCoords) {
        textureCoords = textureCoords.trim();
        if(textureCoords.endsWith(";")) {
            textureCoords = textureCoords.substring(0, textureCoords.length() - 2);
        }
        StringTokenizer stringTokenizer = new StringTokenizer(textureCoords, ";");
        Texture[] textures = new Texture[stringTokenizer.countTokens()];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = createTexture(stringTokenizer.nextToken().trim());
        }

        return textures;
    }

    public int getId() {
        return id;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    void setDisposed(boolean disposed) {
        this.disposed = disposed;
    }

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public String toString() {
        return "TextureAtlas{" +
                "id=" + id +
                ", width=" + width +
                ", height=" + height +
                ", disposed=" + disposed +
                '}';
    }

    public static void main(String[] args) {
        TextureAtlas textureAtlas = new TextureAtlas(0, 1000, 1000);

        Texture[] textures = textureAtlas.createTextures("0,0,16,16; 16,0,16,16; 0,0,16,16; 32,0,16,16");
        System.out.println(Arrays.toString(textures));
    }
}






























