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
package com.ancevt.d2d2world.constant;

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
