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

public abstract class AnimationKey {
	public static final int MAX_ANIMATIONS 	= 13;
	public static final int SLOWING 		= 10;
	
	public static final int IDLE         	= 0;
	public static final int WALK         	= 1;
	public static final int ATTACK       	= 2;
	public static final int JUMP         	= 3;
	public static final int JUMP_ATTACK  	= 4;
	public static final int WALK_ATTACK  	= 5;
	public static final int DAMAGE       	= 6;
	public static final int DEFENSE      	= 7;
	public static final int HOOK         	= 8;
	public static final int HOOK_ATTACK  	= 9;
	public static final int FALL         	= 10;
	public static final int FALL_ATTACK  	= 11;
	public static final int DEATH			= 12;
	public static final int EXTRA_ANIMATION = 13;
}
