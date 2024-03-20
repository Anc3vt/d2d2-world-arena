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

import com.ancevt.d2d2.display.texture.TextureAtlas;
import org.jetbrains.annotations.NotNull;

public final class IntRectangle extends Rectangle<Integer> {

    public IntRectangle() {
        setX(0);
        setY(0);
        setWidth(0);
        setHeight(0);
    }

    public IntRectangle(@NotNull String source) {
        String[] split = source.split(DELIMITER);
        setX(Integer.parseInt(split[0]));
        setY(Integer.parseInt(split[1]));
        setWidth(Integer.parseInt(split[2]));
        setHeight(Integer.parseInt(split[3]));
    }

    public static IntRectangle @NotNull [] getIntRectangles(@NotNull String source) {
        source = TextureAtlas.convertCoords(source);
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
