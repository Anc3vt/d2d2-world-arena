/*
 *   D2D2 World
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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
package ru.ancevt.d2d2world.constant;

public class DataKey {

	public static final String //map & mapkit global properties
        MAP 			= "map",
	 	MAPKIT 			= "mapkit",
		NAME 			= "name",
		ROOM			= "room",
	    ID              = "id",
	    ITEM            = "item",
	    LAYER           = "layer",
	    CLASS           = "class",
	    START_ROOM_ID   = "startRoomId",
		BACKGROUND_COLOR = "backgroundColor",
		MUSIC			= "music";

	public static final String //animation keys
		IDLE            = "idle",
		WALK            = "walk",
		ATTACK          = "attack",
		JUMP            = "jump",
		JUMP_ATTACK     = "jump-attack",
		WALK_ATTACK     = "walk-attack",
		DAMAGE          = "damage",
		DEFENSE         = "defense",
		HOOK            = "hook",
		HOOK_ATTACK     = "hook-attack",
		FALL            = "fall",
		FALL_ATTACK     = "fall-attack",
		EXTRA_ANIMATION = "extra-animation",
		DEATH			= "death";

	public static final String
		SOUND_JUMP		= "snd-jump",
		SOUND_DAMAGE	= "snd-damage",
		SOUND_EXTRA		= "snd-extra",
		SOUND_DEATH		= "snd-death";

}

/* ...: idle, walk, attack, jump, jumpAttack, walkAttack, damage, defense, hook,
 * hookAttack, fall, fallAttack, extraAnimation
 *
 * touchRectangle = X,Y,WxH speed = float maxHealth = int damagingPower = int
 * jumpPower = float
 * type = int
 */