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
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.map.Room;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class WorldEvent extends Event {

    public static final String CHANGE_ROOM = "changeRoom";
    public static final String ACTOR_DEATH = "worldActorDeath";
    public static final String WORLD_PROCESS = "worldProcess";
    public static final String PLAYER_ACTOR_TAKE_BULLET = "playerActorTakeBullet";
    public static final String ROOM_SWITCH_START = "roomSwitchStart";
    public static final String ROOM_SWITCH_COMPLETE = "roomSwitchComplete";
    public static final String ADD_GAME_OBJECT = "addGameObject";
    public static final String REMOVE_GAME_OBJECT = "removeGameObject";
    public static final String BULLET_DOOR_TELEPORT = "bulletDoorTeleport";
    public static final String DESTROYABLE_BOX_DESTROY = "destroyableBoxDestroy";
    public static final String ACTOR_ATTACK = "actorAttack";

    private final float x;
    private final float y;
    private final String roomId;
    private final Weapon.Bullet bullet;
    private final IGameObject gameObject;
    private final Actor actor;
    private final int deadActorGameObjectId;
    private final int killerGameObjectId;
    private final Room room;

}
