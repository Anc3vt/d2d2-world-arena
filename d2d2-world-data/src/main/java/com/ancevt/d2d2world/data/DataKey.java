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

public class DataKey {

    public static final String //map & mapkit global properties
            MAP = "map",
            MAPKIT = "mapkit",
            MAPKIT_NAMES = "mapkitNames",
            ATLAS = "atlas",
            NAME = "name",
            ROOM = "room",
            ID = "id",
            ITEM = "item",
            LAYER = "layer",
            CLASS = "class",
            BACKGROUND_COLOR = "backgroundColor";

    public static final String //animation keys
            IDLE = "idle",
            WALK = "walk",
            ATTACK = "attack",
            JUMP = "jump",
            JUMP_ATTACK = "jumpAttack",
            WALK_ATTACK = "walkAttack",
            DAMAGE = "damage",
            DEFENSE = "defense",
            HOOK = "hook",
            HOOK_ATTACK = "hookAttack",
            FALL = "fall",
            FALL_ATTACK = "fallAttack",
            EXTRA_ANIMATION = "extra-animation",
            DEATH = "death";

    public static final String OWNER_GAME_OBJECT_ID = "ownerGameObjectId";
    public static final String MAX_HEALTH = "maxHealth";
    public static final String HEALTH = "health";
    public static final String HEAD = "head";
    public static final String ARM = "arm";

    public static final String DAMAGE_SOUND = "damageSound";
    public static final String DESTROY_SOUND = "destroySound";
    public static final String BLINK = "blink";
    public static final String BROKEN_PARTS = "brokenParts";
    public static final String OPEN_SOUND = "openSound";
    public static final String CLOSE_SOUND = "closeSound";


    public static String READABLE_NAME = "readableName";
}

/* ...: idle, walk, attack, jump, jumpAttack, walkAttack, damage, defense, hook,
 * hookAttack, fall, fallAttack, extraAnimation
 *
 * touchRectangle = X,Y,WxH speed = float maxHealth = int damagingPower = int
 * jumpPower = float
 * type = int
 */