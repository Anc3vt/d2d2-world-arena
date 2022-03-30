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
package com.ancevt.d2d2world.mapkit;

import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.pickup.Pickup;
import com.ancevt.d2d2world.gameobject.weapon.*;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuiltInMapkit extends Mapkit {

    public static final String UID = "builtin-mapkit";
    public static final String NAME = "builtin-mapkit";
    public static final String ATLAS = "tileset.png";
    public static final String CHARACTER_ATLAS = "character-tileset.png";

    private static BuiltInMapkit instance;

    public static BuiltInMapkit getInstance() {
        return instance == null ? instance = (BuiltInMapkit) MapkitManager.getInstance().getByName(NAME) : instance;
    }

    public Set<MapkitItem> getCharacterMapkitItems() {
        final Set<MapkitItem> items = new HashSet<>();
        keySet().forEach(mapkitItemName->{
            if(mapkitItemName.startsWith("character_")) {
                items.add(getItem(mapkitItemName));
            }
        });
        return items;
    }


    BuiltInMapkit() {
        super(UID, NAME);

        List<String> dataEntries = new ArrayList<>() {
            {
                Set<Class<? extends Pickup>> pickupClasses = findAllSubclassesOfPickup();

                //pickups
                for (Class<?> pickupClass : pickupClasses) {
                    try {
                        add("""
                                name = pickup_""" + pickupClass.getSimpleName() + """
                                | class = """ + pickupClass.getName() + """
                                | collisionX =-8|collisionY =-8|collisionWidth =16|collisionHeight =16
                                | atlas =$ATLAS$
                                | idle = """ + pickupClass.getField("IDLE_COORDS").get(null) + """
                                """);

                    } catch (NoSuchFieldException | IllegalAccessException e) {

                        System.out.println(pickupClass);

                        e.printStackTrace();
                    }

                }

                add("""
                        name = character_blake
                        | readableName = Blake
                        | class =""" + PlayerActor.class.getName() + """
                        | damagePower = 1
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
                        | idle = 96,0,48,48; 144,0,48,48
                        | attack = 96,0,48,48; 144,0,48,48
                        | walk = 192,0,48,48; 240,0,48,48; 288,0,48,48; 336,0,48,48
                        | walkAttack = 0,48,48,48; 48,48,48,48; 96,48,48,48; 144,48,48,48
                        | jump = 192,48,48,48
                        | fall = 240,48,48,48
                        | jumpAttack = 288,48,48,48
                        | fallAttack = 336,48,48,48
                        | hook = 288,96,48,48
                        | hookAttack = 336,96,48,48
                        | damage = 48,144,48,48
                        | head = 240,144,16,24; 256,144,16,24
                        | headFall = 272,144,16,24
                        | arm = 240,176,16,16
                        | damageSound = character-damage.ogg
                        """);

                add("""
                        name = character_ava
                        | readableName = Ava
                        | class =""" + PlayerActor.class.getName() + """
                        | damagePower = 1
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
                        | idle = 96,192,48,48; 144,192,48,48
                        | attack = 96,192,48,48; 144,192,48,48
                        | walk = 192,192,48,48; 240,192,48,48; 288,192,48,48; 336,192,48,48
                        | walkAttack = 0,240,48,48; 48,240,48,48; 96,240,48,48; 144,240,48,48
                        | jump = 192,240,48,48
                        | fall = 240,240,48,48
                        | jumpAttack = 288,240,48,48
                        | fallAttack = 336,240,48,48
                        | hook = 288,288,48,48
                        | hookAttack = 336,288,48,48
                        | damage = 48,336,48,48 
                        | head = 240,336,16,24; 256,336,16,24
                        | headFall = 272,336,16,24
                        | arm = 240,368,16,16
                        | damageSound=character-damage.ogg
                        """);

                add("""
                        name = bullet_of_""" + StandardWeapon.class.getSimpleName() + """
                        | class =""" + StandardWeapon.StandardBullet.class.getName() + """
                        | damagingPower = 15
                        | speed = 5
                        | collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4
                        | atlas = $ATLAS$
                        | idle = 32,0,16,16; 48,0,16,16; 32,0,16,16; 48,0,16,16
                        """);

                add("""
                        name = bullet_of_""" + ArrowWeapon.class.getSimpleName() + """
                        | class =""" + ArrowWeapon.ArrowBullet.class.getName() + """
                        | damagingPower = 20
                        | speed = 8
                        | collisionX = -5 | collisionY = -1 | collisionWidth = 10 | collisionHeight = 3
                        | atlas = $ATLAS$
                        | idle = 32,128,32,16
                        """);

                add("""
                        name = bullet_of_""" + RailWeapon.class.getSimpleName() + """
                        | class =""" + RailWeapon.RailBullet.class.getName() + """
                        | damagingPower = 49
                        | speed = 31
                        | collisionX = -8 | collisionY = -8 | collisionWidth = 16 | collisionHeight = 16
                        | atlas = $ATLAS$
                        | idle = 32,96,32,16
                        """);
                add("""
                        name = bullet_of_""" + AutomaticWeapon.class.getSimpleName() + """
                        | class =""" + AutomaticWeapon.AutomaticBullet.class.getName() + """
                        | damagingPower = 15
                        | speed = 15
                        | collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4
                        | atlas = $ATLAS$
                        | idle = 32,64,32,16
                        """);
                add("""
                        name = bullet_of_""" + PlasmaWeapon.class.getSimpleName() + """
                        | class =""" + PlasmaWeapon.PlasmaBullet.class.getName() + """
                        | damagingPower = 10
                        | speed = 15
                        | collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4
                        | atlas = $ATLAS$
                        | idle = 32,32,32,16
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

    public Set<Class<? extends Pickup>> findAllSubclassesOfPickup() {
        Reflections reflections = new Reflections(Pickup.class.getPackage().getName(), Scanners.SubTypes);
        return new HashSet<>(reflections.getSubTypesOf(Pickup.class));
    }
}
