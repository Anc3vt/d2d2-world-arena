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
package ru.ancevt.d2d2.display.texture;

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
}
