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

import java.util.ArrayList;
import java.util.List;

class DataEntryImpl implements DataEntry {

    private final List<KeyValue> kvs;

    DataEntryImpl() {
        kvs = new ArrayList<>();
    }

    @Override
    public void parse(String source) {
        for (String keyValuePairSource : source.split(DELIMITER)) {
            keyValuePairSource = keyValuePairSource.trim();

            if (keyValuePairSource.contains(EQUALS)) {
                String[] kv = keyValuePairSource.split(EQUALS);
                if (kv.length == 1) {
                    add(kv[0].trim(), ";");
                    continue;
                }
                add(kv[0].trim(), kv[1].trim());
            } else {
                add(keyValuePairSource.trim());
            }
        }
    }

    @Override
    public List<KeyValue> getKeyValues() {
        return List.copyOf(kvs);
    }

    @Override
    public void add(String key) {
        add(key, null);
    }

    @Override
    public void add(String key, Object value) {
        if(key == null) throw new NullPointerException();

        if (containsKey(key)) throw new IllegalStateException("duplicate key: " + key);

        if (value instanceof FloatRectangle rectangle) {
            value = rectangle.stringify();
        }

        kvs.add(new KeyValue(key, value));
    }

    @Override
    public void remove(String key) {
        kvs.removeIf(kv -> kv.key().equals(key));
    }

    @Override
    public boolean containsKey(String key) {
        return kvs.stream().anyMatch(kv -> kv.key().equals(key));
    }

    @Override
    public int size() {
        return kvs.size();
    }

    @Override
    public int getInt(int index) {
        return Integer.parseInt(getString(index));
    }

    @Override
    public float getFloat(int index) {
        return Float.parseFloat(getString(index));
    }

    @Override
    public boolean getBoolean(int index) {
        return Boolean.parseBoolean(getString(index));
    }

    @Override
    public String getString(int index) {
        return String.valueOf(kvs.get(index).key());
    }

    @Override
    public long getLong(int index) {
        return Long.parseLong(getString(index));
    }

    @Override
    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    @Override
    public float getFloat(String key) {
        return Float.parseFloat(getString(key));
    }

    @Override
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    @Override
    public long getLong(String key) {
        return Long.parseLong(getString(key));
    }

    @Override
    public String getString(String key) {
        for (KeyValue kv : kvs)
            if (key.equals(kv.key())) return String.valueOf(kv.value());

        throw new IllegalStateException("no such key: " + key + " in data line " + this);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return !containsKey(key) ? defaultValue : getInt(key);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return !containsKey(key) ? defaultValue : getFloat(key);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return !containsKey(key) ? defaultValue : getBoolean(key);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return !containsKey(key) ? defaultValue : getLong(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return !containsKey(key) ? defaultValue : getString(key);
    }

    @Override
    public FloatRectangle getFloatRectangle(String key) {
        return new FloatRectangle(getString(key));
    }

    @Override
    public FloatRectangle getFloatRectangle(String key, FloatRectangle defaultValue) {
        return !containsKey(key) ? defaultValue : getFloatRectangle(key);
    }

    @Override
    public String stringify() {
        final StringBuilder sb = new StringBuilder();

        for (KeyValue kv : kvs) {
            if (kv.value() != null) {
                sb.append(kv.key()).append(" = ").append(kv.value());
            } else {
                sb.append(kv.key());
            }

            sb.append(" | ");
        }

        if(sb.length() <= 2) {
            return sb.toString();
        }

        return sb.substring(0, sb.toString().length() - 2);
    }

    @Override
    public String toString() {
        return "DataEntryImpl{" + stringify() + '}';
    }


}
