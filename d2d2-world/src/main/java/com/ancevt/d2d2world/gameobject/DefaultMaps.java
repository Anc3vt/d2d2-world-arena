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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.control.Controller;
import com.ancevt.d2d2world.gameobject.area.AreaHook;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DefaultMaps {

    public static void clear() {
        /*
         * Clear all static fields (maps) of this class
         */
        Field[] declaredFields = DefaultMaps.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    Map<IGameObject, Object> map = (Map<IGameObject, Object>) field.get(null);
                    map.clear();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // IGameObject
    static Map<IGameObject, Map<String, Object>> extraMap = new HashMap<>();

    static Map<IGameObject, World> worldMap = new HashMap<>();
    static Map<IGameObject, Integer> gameObjectIdMap = new HashMap<>();
    static Map<IGameObject, MapkitItem> mapkitItemMap = new HashMap<>();

    // ICollision
    static Map<ICollision, Boolean> collisionEnabledMap = new HashMap<>();
    static Map<ICollision, Float> collisionXMap = new HashMap<>();
    static Map<ICollision, Float> collisionYMap = new HashMap<>();
    static Map<ICollision, Float> collisionWidthMap = new HashMap<>();
    static Map<ICollision, Float> collisionHeightMap = new HashMap<>();

    // IControllable
    static Map<IControllable, Controller> controllerMap = new HashMap<>();

    // IDamaging
    static Map<IDamaging, Integer> damagingPowerMap = new HashMap<>();
    static Map<IDamaging, Actor> damagingOwnerActorMap = new HashMap<>();

    // IDestroyable
    static Map<IDestroyable, Integer> maxHealthsMap = new HashMap<>();
    static Map<IDestroyable, Integer> healthMap = new HashMap<>();
    // IDirectioned
    static Map<IDirectioned, Integer> directionedMap = new HashMap<>();
    //
    // IGravitied
    static Map<IGravitational, Float> weightMap = new HashMap<>();
    static Map<IGravitational, ICollision> floorMap = new HashMap<>();
    static Map<IGravitational, Float> velocityXMap = new HashMap<>();
    static Map<IGravitational, Float> velocityYMap = new HashMap<>();
    static Map<IGravitational, Boolean> gravityEnabledMap = new HashMap<>();

    // IHookable
    static Map<IHookable, AreaHook> hookMap = new HashMap<>();

    // IMovable
    static Map<IMovable, Float> startXMap = new HashMap<>();
    static Map<IMovable, Float> startYMap = new HashMap<>();
    static Map<IMovable, Float> movingSpeedXMap = new HashMap<>();
    static Map<IMovable, Float> movingSpeedYMap = new HashMap<>();

    // ISpeedable
    static Map<ISpeedable, Float> speedMap = new HashMap<>();

    // ISynchronized
    static Map<ISynchronized, Boolean> permanentSyncMap = new HashMap<>();

    // ITight
    static Map<ITight, Boolean> floorOnlyMap = new HashMap<>();
    static Map<ITight, Boolean> pushableMap = new HashMap<>();
}





























