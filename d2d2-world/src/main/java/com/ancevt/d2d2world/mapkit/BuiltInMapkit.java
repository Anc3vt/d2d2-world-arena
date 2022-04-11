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
package com.ancevt.d2d2world.mapkit;

import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.gameobject.Fire;
import com.ancevt.d2d2world.gameobject.IdGenerator;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.pickup.Pickup;
import com.ancevt.d2d2world.gameobject.pickup.WeaponPickup;
import com.ancevt.d2d2world.gameobject.weapon.*;
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

    public Set<MapkitItem> getCharacterMapkitItems() {
        final Set<MapkitItem> items = new HashSet<>();
        keySet().forEach(mapkitItemId -> {
            if (mapkitItemId.startsWith("character_")) {
                items.add(getItem(mapkitItemId));
            }
        });
        return items;
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
                        id = fire
                        | class = """ + Fire.class.getName() + """
                        | damagingPower = 5
                        | idle = 288,80,16,16 h 4
                        | collisionX = -8
                        | collisionY = -16
                        | collisionWidth = 16
                        | collisionHeight = 16
                        | atlas=$ATLAS$
                        """);

                add("""
                        id = character_blake
                        | readableName = Blake
                        | class =""" + PlayerActor.class.getName() + """
                        | weight = 0.4
                        | maxHealth = 100
                        | health = 100
                        | speed = 0.75
                        | jumpPower = 3.5
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
                        | class =""" + PlayerActor.class.getName() + """
                        | weight = 0.4
                        | maxHealth = 100
                        | health = 100
                        | speed = 0.75
                        | jumpPower = 3.5
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
                        | class =""" + PlayerActor.class.getName() + """
                        | weight = 0.4
                        | maxHealth = 100
                        | health = 100
                        | speed = 0.75
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
                        | speed = 5
                        | collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4
                        | atlas = $ATLAS$
                        | idle = 32,0,16,16 h 2
                        """);

                add("""
                        id = bullet_of_""" + ArrowWeapon.class.getSimpleName() + """
                        | class =""" + ArrowWeapon.ArrowBullet.class.getName() + """
                        | damagingPower = 20
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
                        | collisionX = -8 | collisionY = -8 | collisionWidth = 16 | collisionHeight = 16
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
                        | damagingPower = 10
                        | speed = 1f
                        | collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4
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

    public static WeaponPickup createWeaponPickupMapkitItem(Weapon weapon) {
        WeaponPickup weaponPickup = (WeaponPickup) getInstance().getItem("pickup_" + WeaponPickup.class.getSimpleName()).createGameObject(IdGenerator.INSTANCE.getNewId());
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
