package com.ancevt.d2d2world.sync;

import com.ancevt.commons.io.ByteInputReader;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.gameobject.*;
import com.ancevt.d2d2world.gameobject.weapon.Bullet;
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

    private ISyncDataReceiver debugSDR;

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
                    case SyncDataType.AIM -> {
                        aim(gameObjectId, in.readFloat(), in.readFloat());
                    }
                    case SyncDataType.ATTACK -> {
                        attack(gameObjectId);
                    }
                    case SyncDataType.ACTION_INDEX -> {
                        actionIndex(gameObjectId, in.readShort());
                    }
                    case SyncDataType.REMOVE -> {
                        removeGameObject(gameObjectId);
                    }
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
                    case SyncDataType.REPAIR -> {
                        repair(gameObjectId);
                    }

                    default -> throw new IllegalStateException("no such SyncDataType " + type);
                }
            } catch (Exception e) {
                log.error("type: " + type, e);
            }
        }

    }

    private void attack(int gameObjectId) {
        if(world.getGameObjectById(gameObjectId) instanceof Actor actor) {
            if(actor instanceof PlayerActor playerActor) {
                if(playerActor.isLocalPlayerActor()) return;
            }
            actor.attack();
        }
    }

    private void aim(int gameObjectId, float aimX, float aimY) {
        if(world.getGameObjectById(gameObjectId) instanceof Actor actor) {
            if(actor instanceof PlayerActor playerActor) {
                if(playerActor.isLocalPlayerActor()) return;
            }
            actor.setAimXY(aimX, aimY);
        }
    }

    private void actionIndex(int gameObjectId, int actionIndex) {
        if(world.getGameObjectById(gameObjectId) instanceof IActioned actioned) {
            actioned.getActionProgram().setCurrentActionIndex(actionIndex);
        }
    }

    private void repair(int gameObjectId) {
        if (world.getGameObjectById(gameObjectId) instanceof IDestroyable destroyable) {
            destroyable.repair();
        }
    }

    private void setVisibility(int gameObjectId, boolean b) {
        world.getGameObjectById(gameObjectId).setVisible(true);
    }

    private void newGameObject(int gameObjectId, int layer, float x, float y, String mapkitName, String mapkitItemId, String dataEntryText) {
        IGameObject gameObject = world.getGameObjectById(gameObjectId);

        boolean needAddToWorld = false;
        if (gameObject == null) {
            Mapkit mapkit = MapkitManager.getInstance().getByName(mapkitName);
            MapkitItem mapkitItem = mapkit.getItem(mapkitItemId);
            gameObject = mapkitItem.createGameObject(gameObjectId);
            needAddToWorld = true;
        }

        gameObject.setXY(x, y);

        DataEntry dataEntry = DataEntry.newInstance(dataEntryText);
        setProperties(gameObject, dataEntry);

        if (gameObject instanceof Bullet bullet) {
            int ownerGameObjectId = dataEntry.getInt(DataKey.OWNER_GAME_OBJECT_ID);
            bullet.setDamagingOwnerActor((Actor) world.getGameObjectById(ownerGameObjectId));
        }

        if (needAddToWorld)
            world.addGameObject(gameObject, layer, false);
    }

    private void removeGameObject(int gameObjectId) {
        IGameObject o = world.getGameObjectById(gameObjectId);
        if (o != null) world.removeGameObject(o, false);
    }

    private void setDirection(int gameObjectId, int direction) {
        if (world.getGameObjectById(gameObjectId) instanceof IDirectioned d) {
            if(d instanceof PlayerActor playerActor) {
                if(playerActor.isLocalPlayerActor()) return;
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

    private synchronized void setXY(int gameObjectId, float x, float y) {
        IGameObject o = world.getGameObjectById(gameObjectId);
        if (o != null) {
            if (o instanceof Bullet) return;
            SyncMotion.moveMotion(o, x, y);
        }
    }

    private void setAnimation(int gameObjectId, int animKey, boolean loop) {
        if (world.getGameObjectById(gameObjectId) instanceof IAnimated a) {
            a.setAnimation(animKey, loop);
        }
    }
}
