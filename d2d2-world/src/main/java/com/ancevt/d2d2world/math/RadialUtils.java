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

package com.ancevt.d2d2world.math;


import org.jetbrains.annotations.NotNull;

import static java.lang.Math.*;

public class RadialUtils {


    public static float @NotNull [] xySpeedOfDegree(float deg) {
        var result = new float[2];
        float rad = (float) (deg * PI / 180);
        result[0] = (float) cos(rad);
        result[1] = (float) sin(rad);
        return result;
    }

    public static int getDirection(float deg) {
        deg += 95f;
        if(deg < 0 || (deg > 180 && deg < 275)) return -1; else return 1;
    }

    public static float getDegreeBetweenPoints(float x1, float y1, float x2, float y2) {
        float rad = (float) Math.atan2(y1 - y2, x2 - x1);
        return (float) (rad * 180 / PI);
    }

    public static float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
}
