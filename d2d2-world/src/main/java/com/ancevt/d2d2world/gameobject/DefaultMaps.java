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





























