package ru.ancevt.d2d2world.net.message;

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

import static ru.ancevt.d2d2world.net.message.SyncDataType.*;

@Slf4j
public class SyncDataReceiver {

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

    public void bytesReceived(byte[] syncData) {
        if (!enabled) return;

        ByteInputReader in = ByteInputReader.newInstance(syncData);

        while (in.hasNextData()) {

            try {
                int type = in.readByte();
                System.out.println(">> " + type);
                int gameObjectId = in.readInt();

                switch (type) {
                    case NEW -> {
                        int layer = in.readByte();
                        String mapkitName = in.readUtf(byte.class);
                        String mapkitItemId = in.readUtf(byte.class);
                        String dataEntryText = in.readUtf(short.class);
                        createGameObject(gameObjectId, layer, mapkitName, mapkitItemId, dataEntryText);
                    }

                    case REMOVE -> {
                        removeGameObject(in.readInt());
                    }

                    case ANIMATION -> {
                        setAnimation(gameObjectId, in.readByte(), in.readByte() == 1);
                    }

                    case XY -> {
                        setXY(gameObjectId, in.readFloat(), in.readFloat());
                    }

                    case HEALTH -> {
                        setHealth(gameObjectId, in.readShort());
                    }

                    case MAX_HEALTH -> {
                        setMaxHealth(gameObjectId, in.readShort());
                    }

                    case DIRECTION -> {
                        setDirection(gameObjectId, in.readByte() - 1);
                    }

                    default -> throw new IllegalStateException("no such SyncDataType " + type);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
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
        if (o != null) o.setXY(x, y);
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

    private void createGameObject(int gameObjectId, int layer, String mapkitName, String mapkitItemId, String dataEntry) {
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
