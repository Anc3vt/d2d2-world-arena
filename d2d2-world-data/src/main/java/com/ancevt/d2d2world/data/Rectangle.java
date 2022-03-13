/*
 *   D2D2 World
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
package com.ancevt.d2d2world.data;

public abstract class Rectangle<T extends Number> {

    protected static final String DELIMITER = ",";

    private T x;

    private T y;

    private T width;

    private T height;

    public T getX() {
        return x;
    }

    void setX(T x) {
        this.x = x;
    }

    public T getY() {
        return y;
    }

    void setY(T y) {
        this.y = y;
    }

    public T getWidth() {
        return width;
    }

    void setWidth(T width) {
        this.width = width;
    }

    public T getHeight() {
        return height;
    }

    void setHeight(T height) {
        this.height = height;
    }

    public String stringify() {
        return x + "," + y + "," + width + "," + height;
    }

}
