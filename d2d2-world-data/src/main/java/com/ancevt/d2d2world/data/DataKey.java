/*
 *   D2D2 World
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
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


    public static String READABLE_NAME = "readableName";
}

/* ...: idle, walk, attack, jump, jumpAttack, walkAttack, damage, defense, hook,
 * hookAttack, fall, fallAttack, extraAnimation
 *
 * touchRectangle = X,Y,WxH speed = float maxHealth = int damagingPower = int
 * jumpPower = float
 * type = int
 */