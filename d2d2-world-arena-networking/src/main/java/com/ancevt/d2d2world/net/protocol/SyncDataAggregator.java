package com.ancevt.d2d2world.net.protocol;

import com.ancevt.commons.io.ByteOutputWriter;
import com.ancevt.d2d2world.gameobject.*;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.sync.ISyncDataAggregator;
import com.ancevt.d2d2world.sync.SyncDataType;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2world.data.Properties.getProperties;

public class SyncDataAggregator implements ISyncDataAggregator {

    private ByteOutputWriter buffer;

    public SyncDataAggregator() {
        buffer = ByteOutputWriter.newInstance();
    }

    @Override
    public synchronized void newGameObject(@NotNull IGameObject gameObject) {
        MapkitItem mapkitItem = gameObject.getMapkitItem();

        buffer.writeByte(SyncDataType.NEW)
                .writeInt(gameObject.getGameObjectId())
                .writeByte(gameObject.getWorld().getLayerByGameObject(gameObject).getIndex())
                .writeFloat(gameObject.getX())
                .writeFloat((gameObject.getY()))
                .writeUtf(byte.class, mapkitItem.getMapkit().getName())
                .writeUtf(byte.class, mapkitItem.getId())
                .writeUtf(short.class, getProperties(gameObject).stringify());
    }

    @Override
    public synchronized void repair(@NotNull IDestroyable destroyable) {
        buffer.writeByte(SyncDataType.REPAIR)
                .writeInt(destroyable.getGameObjectId());
    }

    @Override
    public synchronized void xy(@NotNull IGameObject gameObject) {
        buffer.writeByte(SyncDataType.XY)
                .writeInt(gameObject.getGameObjectId())
                .writeFloat(gameObject.getX())
                .writeFloat(gameObject.getY());
    }

    @Override
    public synchronized void animation(@NotNull IAnimated animated, boolean loop) {
        buffer.writeByte(SyncDataType.ANIMATION)
                .writeInt(animated.getGameObjectId())
                .writeByte(animated.getAnimation())
                .writeByte(loop ? 1 : 0);
    }

    @Override
    public synchronized void health(@NotNull IDestroyable destroyable, IDamaging damaging) {
        buffer.writeByte(SyncDataType.HEALTH)
                .writeInt(destroyable.getGameObjectId())
                .writeShort(destroyable.getHealth())
                .writeInt(damaging != null ? damaging.getGameObjectId() : 0);
    }

    @Override
    public synchronized void maxHealth(@NotNull IDestroyable destroyable) {
        buffer.writeByte(SyncDataType.MAX_HEALTH)
                .writeInt(destroyable.getGameObjectId())
                .writeShort(destroyable.getMaxHealth());
    }

    @Override
    public synchronized void direction(@NotNull IDirectioned directioned) {
        buffer.writeByte(SyncDataType.DIRECTION)
                .writeInt(directioned.getGameObjectId())
                .writeByte(directioned.getDirection() + 1);
    }

    @Override
    public synchronized void visibility(@NotNull IGameObject gameObject, boolean value) {
        buffer.writeByte(SyncDataType.VISIBILITY)
                .writeInt(gameObject.getGameObjectId())
                .writeByte(value ? 1 : 0);
    }

    @Override
    public synchronized void remove(@NotNull IGameObject gameObject) {
        buffer.writeByte(SyncDataType.REMOVE)
                .writeInt(gameObject.getGameObjectId());
    }

    @Override
    public synchronized byte[] createSyncMessage(IGameObject o) {
        newGameObject(o);
        if (o instanceof IAnimated a) {
            animation(a, true);
        }
        if (o instanceof IDirectioned d) {
            direction(d);
        }
        if (o instanceof IDestroyable d) {
            health(d, null);
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
