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

public interface IDestroyable extends ICollision, IResettable {

    @Property
    default void setMaxHealth(int health) {
        DefaultMaps.maxHealthsMap.put(this, health);
    }

    @Property
    default int getMaxHealth() {
        return DefaultMaps.maxHealthsMap.getOrDefault(this, 0);
    }

    @Property
    default void setHealth(int health) {
        if(health < 0) {
            health = 0;
        } else if(health > getMaxHealth()) {
            health = getMaxHealth();
        }

        DefaultMaps.healthMap.put(this, health);
    }

    @Property
    default int getHealth() {
        return DefaultMaps.healthMap.getOrDefault(this, 0);
    }

    void repair();

    void setHealthBy(int health, IDamaging damaging, boolean fromServer);

    void damage(int toHealth, IDamaging damaging);
}
