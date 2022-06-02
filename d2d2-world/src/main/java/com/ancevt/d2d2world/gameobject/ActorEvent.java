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

import com.ancevt.d2d2.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ActorEvent extends Event {

    public static final String SET_WEAPON = "setWeapon";
    public static final String AMMUNITION_CHANGE = "ammunitionChange";
    public static final String ACTOR_DEATH = "actorDeath";
    public static final String ACTOR_REPAIR = "actorRepair";
    public static final String ACTOR_ENTER_ROOM = "actorEnterRoom";
    public static final String ACTOR_HOOK = "actorHook";

    private final String weaponClassName;
    private final int ammunition;
    private final String roomId;
    private final float x;
    private final float y;
    private final int hookGameObjectId;
}
