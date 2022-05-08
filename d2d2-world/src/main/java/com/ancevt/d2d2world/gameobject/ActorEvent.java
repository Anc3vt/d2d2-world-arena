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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ActorEvent extends Event<Actor> {

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
