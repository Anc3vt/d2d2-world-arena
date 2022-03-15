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
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.map.Room;

public class WorldEvent extends Event {

    public static final String CHANGE_ROOM = "changeRoom";
    public static final String ACTOR_DEATH = "actorDeath";
    private final int deadActorGameObjectId;
    private final int killerGameObjectId;

    private final Room room;

    public WorldEvent(String type, World source, Room room, int deadActorGameObjectId, int damagingGameObjectId) {
        super(type, source);
        this.room = room;
        this.deadActorGameObjectId = deadActorGameObjectId;
        this.killerGameObjectId = damagingGameObjectId;
    }

    public Room getRoom() {
        return room;
    }

    public int getDeadActorGameObjectId() {
        return deadActorGameObjectId;
    }

    public int getKillerGameObjectId() {
        return killerGameObjectId;
    }
}