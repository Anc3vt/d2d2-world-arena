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
package com.ancevt.d2d2world.sync;

import com.ancevt.commons.io.ByteInputReader;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.gameobject.Actor;
import com.ancevt.d2d2world.gameobject.IActioned;
import com.ancevt.d2d2world.gameobject.IAnimated;
import com.ancevt.d2d2world.gameobject.IDamaging;
import com.ancevt.d2d2world.gameobject.IDestroyable;
import com.ancevt.d2d2world.gameobject.IDirectioned;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.IResettable;
import com.ancevt.d2d2world.gameobject.ISonicSynchronized;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import com.ancevt.d2d2world.gameobject.area.AreaHook;
import com.ancevt.d2d2world.gameobject.pickup.Pickup;
import com.ancevt.d2d2world.gameobject.weapon.Weapon;
import com.ancevt.d2d2world.mapkit.Mapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.mapkit.MapkitManager;
import com.ancevt.d2d2world.world.World;
import lombok.extern.slf4j.Slf4j;

import static com.ancevt.d2d2world.data.Properties.setProperties;

@Slf4j
public class SyncDataReceiver implements ISyncDataReceiver {

    private World world;
    private boolean enabled;

    public SyncDataReceiver() {
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public synchronized void bytesReceived(byte[] syncData) {
        if (!enabled) return;

        ByteInputReader in = ByteInputReader.newInstance(syncData);

        while (in.hasNextData()) {

            int type = 0;

            try {
                type = in.readByte();

                int gameObjectId = in.readInt();

                switch (type) {
                    case SyncDataType.NEW -> {
                        int layer = in.readByte();
                        float x = in.readFloat();
                        float y = in.readFloat();
                        String mapkitName = in.readUtf(byte.class);
                        String mapkitItemId = in.readUtf(byte.class);
                        String dataEntryText = in.readUtf(short.class);
                        newGameObject(gameObjectId, layer, x, y, mapkitName, mapkitItemId, dataEntryText);
                    }
                    case SyncDataType.PICKUP_DISAPPEAR -> {
                        pickupDisappear(gameObjectId);
                    }
                    case SyncDataType.SOUND -> {
                        String soundFilenameFromMapkit = in.readUtf(byte.class);
                        sound(gameObjectId, soundFilenameFromMapkit);
                    }
                    case SyncDataType.HOOK -> hook(gameObjectId, in.readInt());
                    case SyncDataType.ADD_WEAPON -> {
                        String weaponClassname = in.readUtf(byte.class);
                        addWeapon(gameObjectId, weaponClassname);
                    }
                    case SyncDataType.CHANGE_WEAPON_STATE -> {
                        String weaponClassname = in.readUtf(byte.class);
                        int ammunition = in.readShort();
                        changeWeaponState(gameObjectId, weaponClassname, ammunition);
                    }
                    case SyncDataType.SWITCH_WEAPON -> {
                        String weaponClassname = in.readUtf(byte.class);
                        switchWeapon(gameObjectId, weaponClassname);
                    }
                    case SyncDataType.PICKUP -> {
                        int pickupGameObjectId = in.readInt();
                        pickup(gameObjectId, pickupGameObjectId);
                    }
                    case SyncDataType.AIM -> {
                        aim(gameObjectId, in.readFloat(), in.readFloat());
                    }
                    case SyncDataType.ACTION_INDEX -> {
                        actionIndex(gameObjectId, in.readShort());
                    }
                    case SyncDataType.REMOVE -> removeGameObject(gameObjectId);
                    case SyncDataType.ANIMATION -> {
                        int animKey = in.readByte();
                        boolean loop = in.readByte() == 1;
                        setAnimation(gameObjectId, animKey, loop);
                    }
                    case SyncDataType.XY -> {
                        float x = in.readFloat();
                        float y = in.readFloat();
                        setXY(gameObjectId, x, y);
                    }
                    case SyncDataType.HEALTH -> {
                        int health = in.readShort();
                        int damagingGameObjectId = in.readInt();
                        setHealth(gameObjectId, health, damagingGameObjectId);
                    }
                    case SyncDataType.MAX_HEALTH -> {
                        int maxHealth = in.readShort();
                        setMaxHealth(gameObjectId, maxHealth);
                    }
                    case SyncDataType.DIRECTION -> {
                        int direction = in.readByte() - 1;
                        setDirection(gameObjectId, direction);
                    }
                    case SyncDataType.VISIBILITY -> {
                        boolean visibility = in.readByte() == 1;
                        setVisibility(gameObjectId, visibility);
                    }
                    case SyncDataType.REPAIR -> repair(gameObjectId);
                    case SyncDataType.RESET -> reset(gameObjectId);

                    default -> throw new IllegalStateException("no such SyncDataType " + type);
                }
            } catch (Exception e) {
                log.error("type: " + type, e);
            }
        }

    }

    private void pickupDisappear(int gameObjectId) {
        if (world.getGameObjectById(gameObjectId) instanceof Pickup pickup) {
            pickup.startOut();
        }
    }

    private void sound(int gameObjectId, String soundFilenameFromMapkit) {
        if (world.getGameObjectById(gameObjectId) instanceof ISonicSynchronized sonicSynchronized) {
            sonicSynchronized.playSoundFromServer(soundFilenameFromMapkit);
        }
    }

    private void hook(int gameObjectId, int hookGameObjectId) {
        if (world.getGameObjectById(gameObjectId) instanceof PlayerActor playerActor) {
            AreaHook hook = (AreaHook) world.getGameObjectById(hookGameObjectId);
            playerActor.setHook(hook);
        }
    }

    private void addWeapon(int gameObjectId, String weaponClassname) {
        if (world.getGameObjectById(gameObjectId) instanceof PlayerActor actor) {
            actor.addWeapon(weaponClassname, 0);
        }
    }

    private void pickup(int gameObjectId, int pickupGameObjectId) {
        if (world.getGameObjectById(gameObjectId) instanceof PlayerActor &&
                world.getGameObjectById(pickupGameObjectId) instanceof Pickup pickup) {
            pickup.playPickUpSound();
        }
    }

    private void reset(int gameObjectId) {
        if (world.getGameObjectById(gameObjectId) instanceof IResettable resettable) {
            resettable.reset();
        }
    }

    private void changeWeaponState(int gameObjectId, String weaponClassname, int ammunition) {
        if (world.getGameObjectById(gameObjectId) instanceof Actor actor) {
            actor.setWeaponAmmunition(weaponClassname, ammunition);
        }
    }

    private void switchWeapon(int gameObjectId, String weaponClassname) {
        if (world.getGameObjectById(gameObjectId) instanceof Actor actor) {
            actor.setCurrentWeaponClassname(weaponClassname);
        }
    }

    private void aim(int gameObjectId, float aimX, float aimY) {
        if (world.getGameObjectById(gameObjectId) instanceof Actor actor) {
            if (actor instanceof PlayerActor playerActor) {
                if (playerActor.isLocalPlayerActor()) return;
            }
            actor.setAimXY(aimX, aimY);
        }
    }

    private void actionIndex(int gameObjectId, int actionIndex) {
        if (world.getGameObjectById(gameObjectId) instanceof IActioned actioned) {
            actioned.getActionProgram().setCurrentActionIndex(actionIndex);
        }
    }

    private void repair(int gameObjectId) {
        if (world.getGameObjectById(gameObjectId) instanceof IDestroyable destroyable) {
            destroyable.repair();
        }
    }

    private void setVisibility(int gameObjectId, boolean b) {
        IGameObject gameObject = world.getGameObjectById(gameObjectId);
        if (gameObject != null) {
            world.getGameObjectById(gameObjectId).setVisible(b);
        }
    }

    private void newGameObject(int gameObjectId, int layer, float x, float y, String mapkitName, String mapkitItemId, String dataEntryText) {
        IGameObject gameObject = world.getGameObjectById(gameObjectId);

        boolean needAddToWorld = false;
        if (gameObject == null) {
            Mapkit mapkit = MapkitManager.getInstance().getMapkit(mapkitName);
            MapkitItem mapkitItem = mapkit.getItemById(mapkitItemId);
            gameObject = mapkitItem.createGameObject(gameObjectId);
            needAddToWorld = true;
        }

        gameObject.setXY(x, y);

        DataEntry dataEntry = DataEntry.newInstance(dataEntryText);
        setProperties(gameObject, dataEntry);

        if (gameObject instanceof Weapon.Bullet bullet) {
            int ownerGameObjectId = dataEntry.getInt(DataKey.OWNER_GAME_OBJECT_ID);
            bullet.setDamagingOwnerActor((Actor) world.getGameObjectById(ownerGameObjectId));
        }

        if (needAddToWorld) {
            world.addGameObject(gameObject, layer, false);
        }
    }

    private void removeGameObject(int gameObjectId) {
        IGameObject o = world.getGameObjectById(gameObjectId);
        if (o != null) world.removeGameObject(o, false);
    }

    private void setDirection(int gameObjectId, int direction) {
        if (world.getGameObjectById(gameObjectId) instanceof IDirectioned d) {
            if (d instanceof PlayerActor playerActor) {
                if (playerActor.isLocalPlayerActor()) return;
            }
            d.setDirection(direction);
        }
    }

    private void setHealth(int gameObjectId, int health, int damagingGameObjectId) {
        if (world.getGameObjectById(gameObjectId) instanceof IDestroyable d) {
            IDamaging damaging = (IDamaging) world.getGameObjectById(damagingGameObjectId);
            d.setHealthBy(health, damaging, true);
        }
    }

    private void setMaxHealth(int gameObjectId, int maxHealth) {
        if (world.getGameObjectById(gameObjectId) instanceof IDestroyable d) {
            d.setMaxHealth(maxHealth);
        }
    }

    private void setXY(int gameObjectId, float x, float y) {
        IGameObject o = world.getGameObjectById(gameObjectId);
        if (o != null) {
            if (o instanceof Weapon.Bullet || (o instanceof PlayerActor playerActor && playerActor.isLocalPlayerActor()))
                return;
            //o.setXY(x, y);
            SyncMotion.moveMotion(o, x, y);
        }
    }

    private void setAnimation(int gameObjectId, int animKey, boolean loop) {
        if (world.getGameObjectById(gameObjectId) instanceof IAnimated a) {

            if (a instanceof PlayerActor playerActor) {
                if (playerActor.isLocalPlayerActor()) return;
            }

            a.setAnimation(animKey, loop);
        }
    }
}
