package ru.ancevt.d2d2world.sync;

import lombok.extern.slf4j.Slf4j;
import ru.ancevt.commons.io.ByteInputReader;
import ru.ancevt.d2d2world.constant.AnimationKey;
import ru.ancevt.d2d2world.data.DataEntry;
import ru.ancevt.d2d2world.data.Properties;
import ru.ancevt.d2d2world.gameobject.*;
import ru.ancevt.d2d2world.mapkit.Mapkit;
import ru.ancevt.d2d2world.mapkit.MapkitItem;
import ru.ancevt.d2d2world.mapkit.MapkitManager;
import ru.ancevt.d2d2world.world.World;

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

            try {
                int type = in.readByte();
                int gameObjectId = in.readInt();

                switch (type) {
                    case SyncDataType.NEW -> {
                        int layer = in.readByte();
                        String mapkitName = in.readUtf(byte.class);
                        String mapkitItemId = in.readUtf(byte.class);
                        String dataEntryText = in.readUtf(short.class);
                        newGameObject(gameObjectId, layer, mapkitName, mapkitItemId, dataEntryText);
                    }

                    case SyncDataType.REMOVE -> {
                        removeGameObject(in.readInt());
                    }

                    case SyncDataType.ANIMATION -> {
                        setAnimation(gameObjectId, in.readByte(), in.readByte() == 1);
                    }

                    case SyncDataType.XY -> {
                        setXY(gameObjectId, in.readFloat(), in.readFloat());
                    }

                    case SyncDataType.HEALTH -> {
                        setHealth(gameObjectId, in.readShort());
                    }

                    case SyncDataType.MAX_HEALTH -> {
                        setMaxHealth(gameObjectId, in.readShort());
                    }

                    case SyncDataType.DIRECTION -> {
                        setDirection(gameObjectId, in.readByte() - 1);
                    }

                    default -> throw new IllegalStateException("no such SyncDataType " + type);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void setDirection(int gameObjectId, int direction) {

        if (world.getGameObjectById(gameObjectId) instanceof IDirectioned d) {
            d.setDirection(direction);
        }
    }

    private void setHealth(int gameObjectId, int health) {
        if (world.getGameObjectById(gameObjectId) instanceof IDestroyable d) {
            d.setMaxHealth(health);
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
            SyncMotion.syncMove(o, x, y);
            //o.setXY(x, y);
        }
    }

    private void setAnimation(int gameObjectId, int animKey, boolean loop) {
        if (world.getGameObjectById(gameObjectId) instanceof IAnimated a) {
            a.setAnimation(animKey, loop);
        }
    }

    private void removeGameObject(int gameObjectId) {
        IGameObject o = world.getGameObjectById(gameObjectId);
        if (o != null) world.removeGameObject(o, false);
    }

    private void newGameObject(int gameObjectId, int layer, String mapkitName, String mapkitItemId, String dataEntry) {

        System.out.println(">>> ngo " + gameObjectId);

        Mapkit mapkit = MapkitManager.getInstance().getByName(mapkitName);
        MapkitItem mapkitItem = mapkit.getItem(mapkitItemId);
        IGameObject gameObject = mapkitItem.createGameObject(gameObjectId);
        Properties.setProperties(gameObject, DataEntry.newInstance(dataEntry));

        if (gameObject instanceof IGravitied g) {
            // g.setGravityEnabled(false);
        }

        if (gameObject instanceof IAnimated a) {
            a.setAnimation(AnimationKey.IDLE);
        }

        world.addGameObject(gameObject, layer, false);

    }
}
