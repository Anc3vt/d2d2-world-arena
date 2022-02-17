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
package ru.ancevt.d2d2world.data;

public final class FloatRectangle extends Rectangle<Float>{

    public FloatRectangle() {
        setX(0f);
        setY(0f);
        setWidth(0f);
        setHeight(0f);
    }

    public FloatRectangle(String source) {
        String[] split = source.split(DELIMITER);
        setX(Float.parseFloat(split[0]));
        setY(Float.parseFloat(split[1]));
        setWidth(Float.parseFloat(split[2]));
        setHeight(Float.parseFloat(split[3]));
    }

    @Override
    public String toString() {
        return stringify();
    }
}
