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

public final class IntRectangle extends Rectangle<Integer> {

    public IntRectangle() {
        setX(0);
        setY(0);
        setWidth(0);
        setHeight(0);
    }

    public IntRectangle(String source) {
        String[] split = source.split(DELIMITER);
        setX(Integer.parseInt(split[0]));
        setY(Integer.parseInt(split[1]));
        setWidth(Integer.parseInt(split[2]));
        setHeight(Integer.parseInt(split[3]));
    }

    public static IntRectangle[] getIntRectangles(String source) {
        String[] split = source.split(";");
        IntRectangle[] result = new IntRectangle[split.length];
        for(int i = 0; i < result.length; i ++) {
            split[i] = split[i].trim();

            result[i] = new IntRectangle(split[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        return stringify();
    }
}