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

public interface IGravitational extends IMovable {

    @Property
    default float getWeight() {
        return DefaultMaps.weightMap.getOrDefault(this, 0f);
    }

    @Property
    default void setWeight(float weight) {
        DefaultMaps.weightMap.put(this, weight);
    }

    default void setFloor(ICollision floor) {
        DefaultMaps.floorMap.put(this, floor);
    }

    default ICollision getFloor() {
        return DefaultMaps.floorMap.get(this);
    }

    default void setVelocityX(float velocityX) {
        DefaultMaps.velocityXMap.put(this, velocityX);
    }

    default void setVelocityY(float velocityY) {
        DefaultMaps.velocityYMap.put(this, velocityY);
    }

    default void setVelocity(float vX, float vY) {
        setVelocityX(vX);
        setVelocityY(vY);
    }

    default float getVelocityX() {
        return DefaultMaps.velocityXMap.getOrDefault(this, 0f);
    }

    default float getVelocityY() {
        return DefaultMaps.velocityYMap.getOrDefault(this, 0f);
    }

    @Property
    default void setGravityEnabled(boolean b) {
        DefaultMaps.gravityEnabledMap.put(this, b);
    }

    @Property
    default boolean isGravityEnabled() {
        return DefaultMaps.gravityEnabledMap.getOrDefault(this, false);
    }
}
