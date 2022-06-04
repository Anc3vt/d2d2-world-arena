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
