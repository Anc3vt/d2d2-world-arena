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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2world.gameobject.weapon.StandardWeapon;
import com.ancevt.d2d2world.mapkit.CharacterMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;

public class PlayerActor extends Actor {

    private boolean localPlayerActor;

    public PlayerActor(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);

        BitmapText playerNameBitmapText = new BitmapText();
        playerNameBitmapText.setScale(0.5f, 0.5f);
        add(playerNameBitmapText, 0, -30);
    }

    @Override
    public void onAddToWorld(World world) {
        super.onAddToWorld(world);
        setWeapon(new StandardWeapon(MapkitManager.getInstance().getByName(CharacterMapkit.NAME).getItem("standard_bullet"), this));
    }

    public void setLocalPlayerActor(boolean localPlayerActor) {
        this.localPlayerActor = localPlayerActor;
    }

    public boolean isLocalPlayerActor() {
        return localPlayerActor;
    }
}
