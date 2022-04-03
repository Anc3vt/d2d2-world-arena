package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.control.Controller;
import com.ancevt.d2d2world.gameobject.area.AreaHook;
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
    //static Map<IGameObject, MapkitItem> mapkitItemMap = new HashMap<>();

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

    // IGravitied
    static Map<IGravitied, Float> weightMap = new HashMap<>();
    static Map<IGravitied, ICollision> floorMap = new HashMap<>();
    static Map<IGravitied, Float> velocityXMap = new HashMap<>();
    static Map<IGravitied, Float> velocityYMap = new HashMap<>();
    static Map<IGravitied, Boolean> gravityEnabledMap = new HashMap<>();

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





























