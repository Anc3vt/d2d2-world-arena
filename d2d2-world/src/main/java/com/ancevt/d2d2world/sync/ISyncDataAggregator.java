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
package com.ancevt.d2d2world.sync;

import com.ancevt.d2d2world.gameobject.*;
import com.ancevt.d2d2world.gameobject.area.AreaHook;
import com.ancevt.d2d2world.gameobject.pickup.Pickup;
import org.jetbrains.annotations.NotNull;

public interface ISyncDataAggregator {

    byte[] EMPTY_ARRAY = new byte[]{};

    default void newGameObject(IGameObject gameObject) {
    }

    default void actionIndex(IActioned actioned) {
    }

    default void repair(@NotNull IDestroyable destroyable) {
    }

    default void xy(IGameObject gameObject) {
    }

    default void animation(IAnimated animated, boolean loop) {
    }

    default void health(IDestroyable destroyable, IDamaging damaging) {
    }

    default void maxHealth(IDestroyable destroyable) {
    }

    default void direction(IDirectioned directioned) {
    }

    default void visibility(@NotNull IGameObject gameObject) {

    }

    default void remove(IGameObject gameObject) {
    }

    default void aim(@NotNull Actor actor) {
    }

    default void reset(@NotNull IResettable resettable) {

    }

    default byte[] pullSyncDataMessage() {
        return EMPTY_ARRAY;
    }

    default boolean hasData() {
        return false;
    }

    default void createSyncDataOf(IGameObject o) {
        if (!(o instanceof ISynchronized)) return;

        newGameObject(o);
        xy(o);
        visibility(o);
        if (o instanceof IAnimated a) {
            animation(a, true);
        }
        if (o instanceof IDirectioned d) {
            direction(d);
        }
        if (o instanceof IDestroyable d) {
            health(d, null);
            maxHealth(d);
        }
    }

    default void pickUp(PlayerActor playerActor, int pickupGameObjectId) {
    }

    default void addWeapon(@NotNull Actor actor, String weaponClassname) {
    }

    default void changeWeaponState(Actor actor, String weaponClassname, int ammunition) {
    }

    default void switchWeapon(Actor actor) {
    }

    default void hook(IHookable hookable, AreaHook hook) {
    }

    default void sound(ISonicSynchronized gameObject, String soundFilenameFromMapkit) {};

    default void pickupDisappear(Pickup pickup){};
}
