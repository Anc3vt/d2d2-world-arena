
package com.ancevt.d2d2world.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
}
