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


        if (false) {

            ByteInputReader test = ByteInputReader.newInstance(syncData);

            while (test.hasNextData()) {

                int type = 0;

                try {
                    type = test.readByte();

                    int gameObjectId = test.readInt();

                    switch (type) {
                        case SyncDataType.NEW -> {
                            int layer = test.readByte();
                            float x = test.readFloat();
                            float y = test.readFloat();
                            String mapkitName = test.readUtf(byte.class);
                            String mapkitItemId = test.readUtf(byte.class);
                            String dataEntryText = test.readUtf(short.class);

                            System.out.println("newGameObject " + gameObjectId + ", " + layer + ", " + x + ", " + y + ", " + mapkitName + ", " + mapkitItemId + ", " + dataEntryText);
                        }
                        case SyncDataType.REMOVE -> {
                            System.out.println("removeGameObject " + gameObjectId);
                        }
                        case SyncDataType.ANIMATION -> {
                            int animKey = test.readByte();
                            boolean loop = test.readByte() == 1;
                            System.out.println("setAnimation " + gameObjectId + ", " + animKey + ", " + loop);
                        }
                        case SyncDataType.XY -> {
                            float x = test.readFloat();
                            float y = test.readFloat();
                            System.out.println("setXY " + gameObjectId + ", " + x + ", " + y);
                        }
                        case SyncDataType.HEALTH -> {
                            int health = test.readShort();
                            int damagingGameObjectId = test.readInt();
                            System.out.println("setHealth " + gameObjectId + ", " + health + ", " + damagingGameObjectId);
                        }
                        case SyncDataType.MAX_HEALTH -> {
                            int maxHealth = test.readShort();
                            System.out.println("setMaxHealth " + gameObjectId + ", " + maxHealth);
                        }
                        case SyncDataType.DIRECTION -> {
                            int direction = test.readByte() - 1;
                            setDirection(gameObjectId, direction);
                            System.out.println("setDirection " + gameObjectId + ", " + direction);
                        }
                        case SyncDataType.VISIBILITY -> {
                            boolean visibility = test.readByte() == 1;
                            setVisibility(gameObjectId, visibility);
                            System.out.println("setVisibility " + gameObjectId + ", " + visibility);
                        }
                        case SyncDataType.REPAIR -> {
                            repair(gameObjectId);
                            System.out.println("repair " + gameObjectId);
                        }

                    }
                } catch (Exception e) {

                    //debug("com.ancevt.d2d2world.sync.SyncDataReceiver.bytesReceived(SyncDataReceiver:81):\n<A>" +
                    //Thread.currentThread().getName());
                    //log.error("type: " + type, e);
                }
            }
            System.out.println("------------------");
        }

        ByteInputReader in = ByteInputReader.newInstance(syncData);

        StringBuilder sb = new StringBuilder();

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
                    case SyncDataType.REMOVE -> {
                        removeGameObject(gameObjectId);
                    }
                    case SyncDataType.ANIMATION -> {
                        int animKey = in.readByte();
                        boolean loop = in.readByte() == 1;
                        setAnimation(gameObjectId, animKey, loop);
                    }
                    case SyncDataType.XY -> {
                        sb.append("----- xy\n");
                        sb.append("goid: " + gameObjectId + "\n");
                        float x = in.readFloat();
                        sb.append("x: " + x + "\n");
                        float y = in.readFloat();
                        sb.append("y: " + y + "\n");
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
                System.out.println(sb);
                sb.setLength(0);

                System.exit(1);
            }
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
        Mapkit mapkit = MapkitManager.getInstance().getByName(mapkitName);
        MapkitItem mapkitItem = mapkit.getItem(mapkitItemId);
        IGameObject gameObject = mapkitItem.createGameObject(gameObjectId);
        gameObject.setXY(x, y);

        DataEntry dataEntry = DataEntry.newInstance(dataEntryText);
        setProperties(gameObject, dataEntry);

        if (gameObject instanceof Bullet bullet) {
            int ownerGameObjectId = dataEntry.getInt(DataKey.OWNER_GAME_OBJECT_ID);
            bullet.setDamagingOwnerActor((Actor) world.getGameObjectById(ownerGameObjectId));
        }

        world.addGameObject(gameObject, layer, false);
    }

    private void removeGameObject(int gameObjectId) {
        IGameObject o = world.getGameObjectById(gameObjectId);
        if (o != null) world.removeGameObject(o, false);
    }

    private void setDirection(int gameObjectId, int direction) {

        if (world.getGameObjectById(gameObjectId) instanceof IDirectioned d) {
            d.setDirection(direction);
        }
    }

    private void setHealth(int gameObjectId, int health, int damagingGameObjectId) {
        if (world.getGameObjectById(gameObjectId) instanceof IDestroyable d) {
            IDamaging damaging = (IDamaging) world.getGameObjectById(damagingGameObjectId);
            d.setHealthBy(health, damaging);
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
