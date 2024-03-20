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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.Property;

public interface IMovable extends IResettable {

    @Property
    default void setStartX(float x) {
        DefaultMaps.startXMap.put(this, x);
    }

    @Property
    default void setStartY(float y) {
        DefaultMaps.startYMap.put(this, y);
    }

    default void setStartXY(float x, float y) {
        setStartX(x);
        setStartY(y);
    }

    @Property
    default float getStartX() {
        return DefaultMaps.startXMap.getOrDefault(this, 0f);
    }

    @Property
    default float getStartY() {
        return DefaultMaps.startYMap.getOrDefault(this, 0f);
    }

    default float getMovingSpeedX() {
        return DefaultMaps.movingSpeedXMap.getOrDefault(this, 0f);
    }

    default float getMovingSpeedY() {
        return DefaultMaps.movingSpeedYMap.getOrDefault(this, 0f);
    }

    default void setMovingSpeedX(float value) {
        DefaultMaps.movingSpeedXMap.put(this, value);
    }

    default void setMovingSpeedY(float value) {
        DefaultMaps.movingSpeedYMap.put(this, value);
    }
}
