/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancevt.d2d2world.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DataEntry {

    String DELIMITER = "\\|";
    String EQUALS = "=";

    static @NotNull DataEntry newInstance(String source) {
        DataEntry dataEntry = newInstance();
        dataEntry.parse(source);
        return dataEntry;
    }

    @Contract(value = " -> new", pure = true)
    static @NotNull DataEntry newInstance() {
        return new DataEntryImpl();
    }

    List<KeyValue> getKeyValues();

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
    long getLong(int index);

    int getInt(String key);
    float getFloat(String key);
    boolean getBoolean(String key);
    long getLong(String key);

    String getString(String key);
    int getInt(String key, int defaultValue);
    float getFloat(String key, float defaultValue);
    boolean getBoolean(String key, boolean defaultValue);
    long getLong(String key, long defaultValue);

    String getString(String key, String defaultValue);
    FloatRectangle getFloatRectangle(String key);

    FloatRectangle getFloatRectangle(String key, FloatRectangle defaultValue);

    String stringify();

    record KeyValue(String key, Object value) {
    }
}
