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

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.input.Mouse;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.world.World;
import com.ancevt.d2d2world.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2world.D2D2World.isServer;

public class PlayerActor extends Actor {

    private boolean localPlayerActor;
    private boolean localAim;
    private final Color playerColor;
    private boolean humanControllable;

    public PlayerActor(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);

        playerColor = new Color(0xFFFFFF);

        BitmapText playerNameBitmapText = new BitmapText();
        playerNameBitmapText.setScale(0.5f, 0.5f);
        add(playerNameBitmapText, 0, -30);
    }

    public void setHumanControllable(boolean humanControllable) {
        this.humanControllable = humanControllable;
    }

    public boolean isHumanControllable() {
        return humanControllable;
    }

    @Property
    public void setPlayerColorValue(int color) {
        playerColor.setValue(color);
    }

    @Property
    public int getPlayerColorValue() {
        return playerColor.getValue();
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void setLocalPlayerActor(boolean localPlayerActor) {
        this.localPlayerActor = localPlayerActor;
    }

    public boolean isLocalPlayerActor() {
        return localPlayerActor;
    }

    @Override
    public void onCollide(ICollision collideWith) {
        super.onCollide(collideWith);

        if (!isServer()) {
            if (isLocalPlayerActor() && collideWith instanceof Weapon.Bullet bullet &&
                    bullet.getDamagingOwnerActor() != this &&
                    bullet.getDamagingOwnerActor() instanceof PlayerActor playerActor) {

                getWorld().dispatchEvent(WorldEvent.builder()
                        .type(WorldEvent.PLAYER_ACTOR_TAKE_BULLET)
                        .bullet(bullet)
                        .build());
            }
        }
    }

    @Override
    public void attack() {
        super.attack();

        if (isLocalPlayerActor()) {
            D2D2World.getAim().attack();
        }
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

    private void this_eachFrameLocalAim(Event event) {
        if (isLocalPlayerActor()) {
            if (isOnWorld()) {
                World world = getWorld();

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
    }

    @Override
    public String toString() {
        return "PlayerActor{" +
                "name=" + getName() +
                ", gameObjectId=" + getGameObjectId() +
                ", localPlayerActor=" + localPlayerActor +
                '}';
    }
}
