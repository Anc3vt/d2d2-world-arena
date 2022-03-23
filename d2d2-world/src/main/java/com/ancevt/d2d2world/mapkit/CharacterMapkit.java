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
import com.ancevt.d2d2world.gameobject.weapon.LazerBullet;
import com.ancevt.d2d2world.gameobject.weapon.StandardBullet;

public class CharacterMapkit extends Mapkit {

    public static final String UID = "character-mapkit";
    public static final String NAME = "character-mapkit";

    CharacterMapkit() {
        super(UID, NAME);
        blakeMapkitItem();
        avaMapkitItem();
        standardBulletMapkitItem();
        lazerBulletMapkitItem();
    }

    private void standardBulletMapkitItem() {
        String mapkitData = """
                id = standard_bullet |
                class =""" + StandardBullet.class.getName() + """
                |
                damagingPower = 25 |
                speed = 5 |
                collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4 |
                atlas = bullets.png |
                idle = 32,0,16,16; 48,0,16,16; 32,0,16,16; 48,0,16,16 |
                """;

        DataEntry mapkitDataEntry = DataEntry.newInstance(mapkitData.replace('\n', ' '));
        createItem(mapkitDataEntry);
    }

    private void lazerBulletMapkitItem() {
        String mapkitData = """
                id = lazer_bullet |
                class =""" + LazerBullet.class.getName() + """
                |
                damagingPower = 5 |
                speed = 15 |
                collisionX = -2 | collisionY = -2 | collisionWidth = 4 | collisionHeight = 4 |
                atlas = bullets.png |
                idle = 32,32,32,16 |
                """;

        DataEntry mapkitDataEntry = DataEntry.newInstance(mapkitData.replace('\n', ' '));
        createItem(mapkitDataEntry);
    }

    private void blakeMapkitItem() {
        String mapkitData = """
                id = character_blake |
                class =""" + PlayerActor.class.getName() + """
                |
                damagePower = 1 |
                weight = 0.4 |
                maxHealth = 100 |
                health = 100 |
                speed = 0.75 |
                jumpPower = 3.5 |
                collisionX = -6 |
                collisionY = -12 |
                collisionWidth = 12 |
                collisionHeight = 28 |
                
                weaponX=15 |
                weaponY=-3 |
                atlas=blake-and-ava-tileset.png |
                idle = 96,0,48,48; 144,0,48,48 |
                attack = 96,0,48,48; 144,0,48,48 |
                walk = 192,0,48,48; 240,0,48,48; 288,0,48,48; 336,0,48,48 |
                walk-attack = 0,48,48,48; 48,48,48,48; 96,48,48,48; 144,48,48,48 |
                jump = 192,48,48,48 |
                fall = 240,48,48,48 |
                jump-attack = 288,48,48,48 |
                fall-attack = 336,48,48,48 |
                hook = 288,96,48,48 |
                hook-attack = 336,96,48,48 |
                damage = 48,144,48,48 |
                head = 240,144,16,24; 256,144,16,24 |
                headFall = 272,144,16,24 |
                arm = 240,176,16,16 |
                """;

        DataEntry mapkitDataEntry = DataEntry.newInstance(mapkitData.replace('\n', ' '));
        createItem(mapkitDataEntry);
    }

    private void avaMapkitItem() {
        String mapkitData = """
                id = character_ava |
                class =""" + PlayerActor.class.getName() + """
                |
                damagePower = 1 |
                weight = 0.4 |
                maxHealth = 100 |
                health = 100 |
                speed = 0.75 |
                jumpPower = 3.5 |
                collisionX = -6 |
                collisionY = -12 |
                collisionWidth = 12 |
                collisionHeight = 28 |
                
                weaponX=11 |
                weaponY=-3 |
                atlas=blake-and-ava-tileset.png |
                idle = 96,192,48,48; 144,192,48,48 |
                attack = 96,192,48,48; 144,192,48,48 |
                walk = 192,192,48,48; 240,192,48,48; 288,192,48,48; 336,192,48,48 |
                walk-attack = 0,240,48,48; 48,240,48,48; 96,240,48,48; 144,240,48,48 |
                jump = 192,240,48,48 |
                fall = 240,240,48,48 |
                jump-attack = 288,240,48,48 |
                fall-attack = 336,240,48,48 |
                hook = 288,288,48,48 |
                hook-attack = 336,288,48,48 |
                damage = 48,336,48,48 |
                head = 240,336,16,24; 256,336,16,24 |
                headFall = 272,336,16,24 |
                arm = 240,368,16,16 |
                """;

        DataEntry mapkitDataEntry = DataEntry.newInstance(mapkitData.replace('\n', ' '));
        createItem(mapkitDataEntry);
    }

}
