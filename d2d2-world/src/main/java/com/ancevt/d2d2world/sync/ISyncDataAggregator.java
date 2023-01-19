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

    default void animation(IAnimated_old animated, boolean loop) {
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
        if (o instanceof IAnimated_old a) {
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

    default void pickUp(PlayerActor_ playerActor, int pickupGameObjectId) {
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
