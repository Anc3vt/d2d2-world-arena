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
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.gameobject.weapon.AutomaticWeapon;
import com.ancevt.d2d2world.gameobject.weapon.PlasmaWeapon;
import com.ancevt.d2d2world.gameobject.weapon.RailWeapon;
import com.ancevt.d2d2world.gameobject.weapon.StandardWeapon;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;

public class PlayerActor extends Actor {

    private boolean localPlayerActor;
    private boolean localAim;

    public PlayerActor(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);

        BitmapText playerNameBitmapText = new BitmapText();
        playerNameBitmapText.setScale(0.5f, 0.5f);
        add(playerNameBitmapText, 0, -30);

        addWeapon(new StandardWeapon(), 100);
        addWeapon(new PlasmaWeapon(), 100);
        addWeapon(new AutomaticWeapon(), 100);
        addWeapon(new RailWeapon(), 100);
        setWeapon(getWeapons().get(0));
    }

    @Override
    public void onAddToWorld(World world) {
        super.onAddToWorld(world);
        setWeapon(new StandardWeapon());
    }

    @Override
    public boolean isFloorOnly() {
        return true;
    }

    public void setLocalPlayerActor(boolean localPlayerActor) {
        this.localPlayerActor = localPlayerActor;
    }

    public boolean isLocalPlayerActor() {
        return localPlayerActor;
    }

    public void setLocalAim(boolean value) {
        this.localAim = value;
        if (value) {
            addEventListener("localAim", Event.EACH_FRAME, this::this_eachFrameLocalAim);
        } else {
            removeAllEventListeners("localAim");
        }
    }

    public boolean isLocalAim() {
        return localAim;
    }

    @Override
    public void attack() {
        super.attack();

        if(isLocalPlayerActor()) {
            D2D2World.getAim().attack();
        }
    }

    private void this_eachFrameLocalAim(Event event) {
        final World world = getWorld();

        float scaleX = world.getAbsoluteScaleX();
        float scaleY = world.getAbsoluteScaleY();

        float wx = world.getAbsoluteX() / scaleX;
        float wy = world.getAbsoluteY() / scaleY;

        float x = Mouse.getX() / scaleX;
        float y = Mouse.getY() / scaleY;

        float worldX = (x - wx);
        float worldY = (y - wy);

        setAimXY(worldX, worldY);
    }
}
