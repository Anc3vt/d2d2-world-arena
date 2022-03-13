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

public interface DataEntry {

    String DELIMITER = "\\|";
    String EQUALS = "=";

    static DataEntry newInstance(String source) {
        DataEntry dataEntry = newInstance();
        dataEntry.parse(source);
        return dataEntry;
    }

    static DataEntry newInstance() {
        return new DataEntryImpl();
    }

    void parse(String source);

    void add(String key);
    void add(String key, Object value);
    void remove(String key);

    boolean containsKey(String key);
    int size();

    int getInt(int index);
    float getFloat(int index);
    boolean getBoolean(int index);
    String getString(int index);

    int getInt(String key);
    float getFloat(String key);
    boolean getBoolean(String key);
    String getString(String key);

    int getInt(String key, int defaultValue);
    float getFloat(String key, float defaultValue);
    boolean getBoolean(String key, boolean defaultValue);
    String getString(String key, String defaultValue);

    FloatRectangle getFloatRectangle(String key);
    FloatRectangle getFloatRectangle(String key, FloatRectangle defaultValue);

    String stringify();

}
