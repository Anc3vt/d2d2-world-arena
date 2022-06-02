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

package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.input.Mouse;
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
    private String playerName;
    private int playerId;

    public PlayerActor(@NotNull MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);

        playerColor = new Color(0xFFFFFF);

        BitmapText playerNameBitmapText = new BitmapText();
        playerNameBitmapText.setScale(0.5f, 0.5f);
        add(playerNameBitmapText, 0, -30);
    }

    @Property
    public int getPlayerId() {
        return playerId;
    }

    @Property
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Property
    public String getPlayerName() {
        return playerName;
    }

    @Property
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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

    public void setLocalAim(boolean value) {
        this.localAim = value;
        if (value) {
            addEventListener(this, Event.EACH_FRAME, this::this_eachFrameLocalAim);
        } else {
            removeEventListener(this, Event.EACH_FRAME);
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
