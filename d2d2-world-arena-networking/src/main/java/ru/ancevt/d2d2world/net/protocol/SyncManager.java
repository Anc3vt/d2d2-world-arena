package ru.ancevt.d2d2world.net.protocol;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.io.ByteOutputWriter;
import ru.ancevt.d2d2world.gameobject.IAnimated;
import ru.ancevt.d2d2world.gameobject.IDestroyable;
import ru.ancevt.d2d2world.gameobject.IDirectioned;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.mapkit.MapkitItem;
import ru.ancevt.d2d2world.net.message.MessageType;
import ru.ancevt.d2d2world.net.message.SyncDataType;
import ru.ancevt.d2d2world.sync.ISyncManager;

import static ru.ancevt.d2d2world.data.Properties.getProperties;

public class SyncManager implements ISyncManager {

    private ByteOutputWriter buffer;

    public SyncManager() {
        buffer = ByteOutputWriter.newInstance();
    }

    @Override
    public void newGameObject(@NotNull IGameObject gameObject) {
        MapkitItem mapkitItem = gameObject.getMapkitItem();

        buffer.writeByte(SyncDataType.NEW)
                .writeInt(gameObject.getGameObjectId())
                .writeByte(gameObject.getWorld().getLayerByGameObject(gameObject).getIndex())
                .writeUtf(byte.class, mapkitItem.getMapkit().getName())
                .writeUtf(byte.class, mapkitItem.getId())
                .writeUtf(short.class, getProperties(gameObject).stringify())
                .toByteArray();
    }

    @Override
    public void xy(@NotNull IGameObject gameObject) {
        // System.out.println(">> xy " + gameObject);

        buffer.writeByte(SyncDataType.XY)
                .writeInt(gameObject.getGameObjectId())
                .writeFloat(gameObject.getX())
                .writeFloat(gameObject.getY())
                .toByteArray();
    }

    @Override
    public void animation(@NotNull IAnimated animated, boolean loop) {
        buffer.writeByte(SyncDataType.ANIMATION).
                writeInt(animated.getGameObjectId())
                .writeByte(animated.getAnimation())
                .writeByte(loop ? 1 : 0)
                .toByteArray();
    }

    @Override
    public void health(@NotNull IDestroyable destroyable) {
        buffer.writeByte(SyncDataType.HEALTH)
                .writeInt(destroyable.getGameObjectId())
                .writeShort(destroyable.getHealth())
                .toByteArray();
    }

    @Override
    public void maxHealth(@NotNull IDestroyable destroyable) {
        buffer.writeByte(SyncDataType.MAX_HEALTH)
                .writeInt(destroyable.getGameObjectId())
                .writeShort(destroyable.getMaxHealth())
                .toByteArray();
    }

    @Override
    public void direction(@NotNull IDirectioned directioned) {
        buffer.writeByte(SyncDataType.DIRECTION)
                .writeInt(directioned.getGameObjectId())
                .writeByte(directioned.getDirection() + 1)
                .toByteArray();
    }

    @Override
    public void remove(@NotNull IGameObject gameObject) {
        buffer.writeByte(SyncDataType.REMOVE)
                .writeInt(gameObject.getGameObjectId())
                .toByteArray();
    }

    @Override
    public synchronized byte[] createSyncMessage(IGameObject o) {
        newGameObject(o);
        xy(o);
        if (o instanceof IAnimated a) {
            animation(a, true);
        }
        if (o instanceof IDirectioned d) {
            direction(d);
        }
        if (o instanceof IDestroyable d) {
            health(d);
            maxHealth(d);
        }

        byte[] data = buffer.toByteArray();

        byte[] result = ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_SYNC_DATA)
                .writeShort(data.length)
                .writeBytes(data)
                .toByteArray();

        buffer = ByteOutputWriter.newInstance();

        return result;
    }

    @Override
    public synchronized byte[] createSyncMessage() {
        byte[] data = buffer.toByteArray();

        byte[] result = ByteOutputWriter.newInstance()
                .writeByte(MessageType.SERVER_SYNC_DATA)
                .writeShort(data.length)
                .writeBytes(data)
                .toByteArray();

        buffer = ByteOutputWriter.newInstance();

        return result;
    }

    @Override
    public boolean hasData() {
        return buffer.hasData();
    }
}
