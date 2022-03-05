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
package ru.ancevt.d2d2world.gameobject.character;

import ru.ancevt.d2d2world.data.DataEntry;
import ru.ancevt.d2d2world.gameobject.PlayerActor;
import ru.ancevt.d2d2world.mapkit.CharacterMapkit;
import ru.ancevt.d2d2world.mapkit.MapkitItem;
import ru.ancevt.d2d2world.mapkit.MapkitManager;

import static ru.ancevt.d2d2world.data.Properties.setProperties;

public class Ava extends PlayerActor {

    private static DataEntry mapkitDataEntry;
    private static MapkitItem mapkitItem;

    public Ava() {
        super(getOrCreateMapkitItem());
        setProperties(this, mapkitDataEntry);
    }

    private static MapkitItem getOrCreateMapkitItem() {
        if(mapkitItem != null) return mapkitItem;

        String mapkitData = """
                id = 2 |
                damagePower = 1 |
                weight = 1 |
                maxHealth = 100 |
                health = 100 |
                speed = 0.75 |
                jumpPower = 8 |
                collisionX = -6 |
                collisionY = -12 |
                collisionWidth = 12 |
                collisionHeight = 28 |
                atlas=blake-and-ava-tileset.png |
                                
                idle = 0,192,48,48; 48,192,48,48 |
                attack = 96,192,48,48; 144,192,48,48 |
                walk = 192,192,48,48; 240,192,48,48; 288,192,48,48; 336,192,48,48 |
                walk-attack = 0,240,48,48; 48,240,48,48; 96,240,48,48; 144,240,48,48 |
                jump = 192,240,48,48 |
                fall = 240,240,48,48 |
                jump-attack = 288,240,48,48 |
                fall-attack = 336,240,48,48 |
                hook = 288,288,48,48 |
                hook-attack = 336,288,48,48 |
                damage = 48,336,48,48
                """;

        mapkitDataEntry = DataEntry.newInstance(mapkitData.replace('\n', ' '));
        mapkitItem = MapkitManager.getInstance().get(CharacterMapkit.ID).createItem(mapkitDataEntry);

        return mapkitItem;
    }

}