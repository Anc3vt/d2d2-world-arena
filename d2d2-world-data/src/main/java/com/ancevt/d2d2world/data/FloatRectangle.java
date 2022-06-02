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
