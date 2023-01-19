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
package com.ancevt.d2d2world.mapkit;

import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.gameobject.Animated;
import com.ancevt.d2d2world.gameobject.IdGenerator;
import com.ancevt.d2d2world.gameobject.PlayerActor_;
import com.ancevt.d2d2world.gameobject.Scenery;
import com.ancevt.d2d2world.gameobject.SceneryRect;
import com.ancevt.d2d2world.gameobject.pickup.Pickup;
import com.ancevt.d2d2world.gameobject.pickup.WeaponPickup;
import com.ancevt.d2d2world.gameobject.weapon.ArrowWeapon;
import com.ancevt.d2d2world.gameobject.weapon.AutomaticWeapon;
import com.ancevt.d2d2world.gameobject.weapon.FireWeapon;
import com.ancevt.d2d2world.gameobject.weapon.PlasmaWeapon;
import com.ancevt.d2d2world.gameobject.weapon.RailWeapon;
import com.ancevt.d2d2world.gameobject.weapon.RaveWeapon;
import com.ancevt.d2d2world.gameobject.weapon.StandardWeapon;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuiltInMapkit extends Mapkit {

    public static final String NAME = "builtin-mapkit";
    public static final String ATLAS = "tileset.png";
    public static final String CHARACTER_ATLAS = "character-tileset-1.png";

    private static BuiltInMapkit instance;

    public static BuiltInMapkit getInstance() {
        return instance == null ? instance = (BuiltInMapkit) MapkitManager.getInstance().getMapkit(NAME) : instance;
    }

    public List<MapkitItem> getCharacterMapkitItems() {
        return getItems().stream()
                .filter(mapkitItem -> mapkitItem.getId().startsWith("character_"))
                .toList();
    }


    BuiltInMapkit() {
        super(NAME);

        List<String> dataEntries = new ArrayList<>() {
            {
                Set<Class<? extends Pickup>> pickupClasses = findAllSubclassesOfPickup();

                //pickups
                for (Class<?> pickupClass : pickupClasses) {
                    try {
                        add("""
                                id = pickup_""" + pickupClass.getSimpleName() + """
                                | class = """ + pickupClass.getName() + """
                                | collisionX =-8|collisionY =-8|collisionWidth =16|collisionHeight =16
                                | atlas =$ATLAS$
                                | idle = """ + pickupClass.getField("IDLE_COORDS").get(null) + """
                                """);

                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        System.err.println(pickupClass);
                        e.printStackTrace();
                    }
                }

                add("""
                        id = scenery_rect 
                        | class = """ + SceneryRect.class.getName() + """
                        | idle = 0,0,0,0
                        | atlas = $ATLAS$
                        | width = 16
                        | height = 16
                        """);

                add("""
                        id = water_scenery_surface
                        | class = """ + Scenery.class.getName() + """
                        | idle = 112,16,16,4 v 4
                        | atlas=$ATLAS$
                        """);
                add("""
                        id = water_scenery
                        | class = """ + Scenery.class.getName() + """
                        | idle = 128,16,16,16
                        | static = false
                        | bleedingFix = 0.00005
                        | atlas=$ATLAS$
                        """);
                add("""
                        id = character_blake
                        | readableName = Blake
                        | class =""" + PlayerActor_.class.getName() + """
                        | weight = 0.4
                        | maxHealth = 100
                        | health = 100
                        | speed = 0.65
                        | jumpPower = 3.0
                        | collisionX = -6
                        | collisionY = -12
                        | collisionWidth = 12
                        | collisionHeight = 28
                        | weaponX=15
                        | weaponY=-3
                        | atlas=$CHARACTER_ATLAS$
                        | idle = 0,0,48,48 h 2
                        | attack = 0,0,48,48 h 2
                        | walk = 96,0,48,48 h 4
                        | walkAttack = 96,0,48,48 h 4
                        | jump = 288,0,48,48
                        | fall = 336,0,48,48
                        | jumpAttack = 288,0,48,48
                        | fallAttack = 336,0,48,48
                        | hook = 384,0,48,48
                        | hookAttack = 384,0,48,48
                        | damage = 432,0,48,48
                        | head = 576,0,16,24 h 2
                        | headFall = 608,0,16,24
                        | arm = 576,32,16,16
                        | damageSound = character-damage.ogg
                        """);
                add("""
                          id = blake
                        | readableName = Blake
                        | class =""" + Animated.class.getName() + """
                          width = 48
                          height = 48
                          
                          animation:idle=0,2
                          animation:walk=1,4
                          animation:jump=2,1
                          animation:fall=3,1
                          animation:hook=4,1
                          animation:damage=5,1
                          
                          _handCoords=0,288,16,16
                          _headCoords=16,288,16,32 h 2
                          
                        | weight = 0.4
                        | maxHealth = 100
                        | health = 100
                        | speed = 0.65
                        | jumpPower = 3.0
                        | collisionX = -6
                        | collisionY = -12
                        | collisionWidth = 12
                        | collisionHeight = 28
                        | weaponX=15
                        | weaponY=-3
                        | atlas=$CHARACTER_ATLAS$
                        | idle = 0,0,48,48 h 2
                        | attack = 0,0,48,48 h 2
                        | walk = 96,0,48,48 h 4
                        | walkAttack = 96,0,48,48 h 4
                        | jump = 288,0,48,48
                        | fall = 336,0,48,48
                        | jumpAttack = 288,0,48,48
                        | fallAttack = 336,0,48,48
                        | hook = 384,0,48,48
                        | hookAttack = 384,0,48,48
                        | damage = 432,0,48,48
                        | head = 576,0,16,24 h 2
                        | headFall = 608,0,16,24
                        | arm = 576,32,16,16
                        | damageSound = character-damage.ogg
                        """);
                add("""
                        id = character_ava
                        | readableName = Ava
                        | class =""" + PlayerActor_.class.getName() + """
                        | weight = 0.4
                        | maxHealth = 100
                        | health = 100
                        | speed = 0.65
                        | jumpPower = 3.0
                        | collisionX = -6
                        | collisionY = -12
                        | collisionWidth = 12
                        | collisionHeight = 28
                        | weaponX=14
                        | weaponY=-3
                        | atlas=$CHARACTER_ATLAS$
                        | idle = 0,48,48,48 h 2
                        | attack = 0,48,48,48 h 2
                        | walk = 96,48,48,48 h 4
                        | walkAttack = 96,48,48,48 h 4
                        | jump = 288,48,48,48
                        | fall = 336,48,48,48
                        | jumpAttack = 288,48,48,48
                        | fallAttack = 336,48,48,48
                        | hook = 384,48,48,48
                        | hookAttack = 384,48,48,48
                        | damage = 432,48,48,48
                        | head = 576,48,16,24 h 2
                        | headFall = 608,48,16,24
                        | arm = 576,80,16,16
                        | damageSound=character-damage.ogg
                        """);
                add("""
                        id = character_stranger
                        | readableName = Stranger
                        | class =""" + PlayerActor_.class.getName() + """
                        | weight = 0.4
                        | maxHealth = 100
                        | health = 100
                        | speed = 0.65
                        | jumpPower = 3.5
                        | collisionX = -6
                        | collisionY = -12
                        | collisionWidth = 12
                        | collisionHeight = 28
                        | weaponX=14
                        | weaponY=-3
                        | atlas=$CHARACTER_ATLAS$
                        | idle = 0,96,48,48 h 2
                        | attack = 0,96,48,48 h 2
                        | walk = 96,96,48,48 h 4
                        | walkAttack = 96,96,48,48 h 4
                        | jump = 288,96,48,48
                        | fall = 336,96,48,48
                        | jumpAttack = 288,96,48,48
                        | fallAttack = 336,96,48,48
                        | hook = 384,96,48,48
                        | hookAttack = 384,96,48,48
                        | damage = 432,96,48,48
                        | head = 576,96,16,24 h 2
                        | headFall = 608,96,16,24
                        | arm = 576,128,16,16
                        | damageSound=character-damage.ogg
                        """);
                add("""
                        id = bullet_of_""" + StandardWeapon.class.getSimpleName() + """
                        | class =""" + StandardWeapon.StandardBullet.class.getName() + """
                        | damagingPower = 15
                        | speed = 10
                        | collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4
                        | atlas = $ATLAS$
                        | idle = 32,0,16,16 h 2
                        """);
                add("""
                        id = bullet_of_""" + RaveWeapon.class.getSimpleName() + """
                        | class =""" + RaveWeapon.RaveWeaponBullet.class.getName() + """
                        | damagingPower = 15
                        | speed = 12d
                        | collisionX = -5 | collisionY = -2 | collisionWidth = 10 | collisionHeight = 4
                        | atlas = $ATLAS$
                        | idle = 32,144,32,16 h 4
                        """);
                add("""
                        id = bullet_of_""" + ArrowWeapon.class.getSimpleName() + """
                        | class =""" + ArrowWeapon.ArrowBullet.class.getName() + """
                        | damagingPower = 33
                        | speed = 8
                        | collisionX = -5 | collisionY = -1 | collisionWidth = 10 | collisionHeight = 3
                        | atlas = $ATLAS$
                        | idle = 32,64,32,16
                        | death = 64,64,32,16
                        """);

                add("""
                        id = bullet_of_""" + RailWeapon.class.getSimpleName() + """
                        | class =""" + RailWeapon.RailBullet.class.getName() + """
                        | damagingPower = 49
                        | speed = 32
                        | collisionX = -8 | collisionY = -2 | collisionWidth = 16 | collisionHeight = 4
                        | atlas = $ATLAS$
                        | idle = 32,48,32,16
                        """);
                add("""
                        id = bullet_of_""" + AutomaticWeapon.class.getSimpleName() + """
                        | class =""" + AutomaticWeapon.AutomaticBullet.class.getName() + """
                        | damagingPower = 15
                        | speed = 15
                        | collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4
                        | atlas = $ATLAS$
                        | idle = 32,32,32,16
                        """);
                add("""
                        id = bullet_of_""" + PlasmaWeapon.class.getSimpleName() + """
                        | class =""" + PlasmaWeapon.PlasmaBullet.class.getName() + """
                        | damagingPower = 10
                        | speed = 15
                        | collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4
                        | atlas = $ATLAS$
                        | idle = 32,16,32,16
                        """);

                add("""
                        id = bullet_of_""" + FireWeapon.class.getSimpleName() + """
                        | class =""" + FireWeapon.FireBullet.class.getName() + """
                        | damagingPower = 20
                        | speed = 1f
                        | collisionX = -8 | collisionY = -16 | collisionWidth = 32 | collisionHeight = 32
                        | atlas = $ATLAS$
                        | idle = 32,80,64,64 h 4
                        """);

            }
        };

        for (String dataEntryText : dataEntries) {
            DataEntry mapkitDataEntry = DataEntry.newInstance(dataEntryText
                    .replace('\n', ' ')
                    .replace("$ATLAS$", ATLAS)
                    .replace("$CHARACTER_ATLAS$", CHARACTER_ATLAS)
            );
            createItem(mapkitDataEntry);
        }
    }

    public static @NotNull WeaponPickup createWeaponPickupMapkitItem(@NotNull Weapon weapon) {
        WeaponPickup weaponPickup = (WeaponPickup) getInstance().getItemById("pickup_" + WeaponPickup.class.getSimpleName()).createGameObject(IdGenerator.getInstance().getNewId());
        weaponPickup.setWeaponClassname(weapon.getClass().getName());
        weaponPickup.setAmmunition(weapon.getAmmunition());
        weaponPickup.setRespawnTimeMillis(0);
        return weaponPickup;
    }

    public Set<Class<? extends Pickup>> findAllSubclassesOfPickup() {
        Reflections reflections = new Reflections(Pickup.class.getPackage().getName(), Scanners.SubTypes);
        return new HashSet<>(reflections.getSubTypesOf(Pickup.class));
    }

}























