package com.ancevt.d2d2world.net.protocol;

import com.ancevt.commons.io.ByteOutputWriter;
import com.ancevt.d2d2world.gameobject.*;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import com.ancevt.d2d2world.net.message.MessageType;
import com.ancevt.d2d2world.sync.ISyncDataAggregator;
import com.ancevt.d2d2world.sync.SyncDataType;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.commons.unix.UnixDisplay.debug;
import static com.ancevt.d2d2world.data.Properties.getProperties;

public class SyncDataAggregator implements ISyncDataAggregator {

    private ByteOutputWriter buffer;

    public SyncDataAggregator() {
        buffer = ByteOutputWriter.newInstance();
    }

    @Override
    public synchronized void newGameObject(@NotNull IGameObject gameObject) {
        if (!(gameObject instanceof ISynchronized)) return;

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
    public synchronized void weapon(@NotNull Actor actor) {
        buffer.writeByte(SyncDataType.WEAPON)
                .writeInt(actor.getGameObjectId())
                .writeUtf(byte.class, actor.getWeapon().getClass().getName());
    }

    @Override
    public synchronized void actionIndex(IActioned actioned) {
        if (!(actioned instanceof ISynchronized)) return;

        buffer.writeByte(SyncDataType.ACTION_INDEX)
                .writeInt(actioned.getGameObjectId())
                .writeShort(actioned.getActionProgram().getCurrentActionIndex());
    }

    @Override
    public synchronized void repair(@NotNull IDestroyable destroyable) {
        if (!(destroyable instanceof ISynchronized)) return;

        buffer.writeByte(SyncDataType.REPAIR)
                .writeInt(destroyable.getGameObjectId());
    }

    @Override
    public synchronized void xy(@NotNull IGameObject gameObject) {
        if (!(gameObject instanceof ISynchronized)) return;

        buffer.writeByte(SyncDataType.XY)
                .writeInt(gameObject.getGameObjectId())
                .writeFloat(gameObject.getX())
                .writeFloat(gameObject.getY());
    }

    @Override
    public synchronized void animation(@NotNull IAnimated animated, boolean loop) {
        if (!(animated instanceof ISynchronized)) return;

        buffer.writeByte(SyncDataType.ANIMATION)
                .writeInt(animated.getGameObjectId())
                .writeByte(animated.getAnimation())
                .writeByte(loop ? 1 : 0);
    }

    @Override
    public synchronized void health(@NotNull IDestroyable destroyable, IDamaging damaging) {
        if (damaging != null && !(damaging instanceof ISynchronized)) return;

        buffer.writeByte(SyncDataType.HEALTH)
                .writeInt(destroyable.getGameObjectId())
                .writeShort(destroyable.getHealth())
                .writeInt(damaging != null ? damaging.getGameObjectId() : 0);
    }

    @Override
    public synchronized void maxHealth(@NotNull IDestroyable destroyable) {
        if (!(destroyable instanceof ISynchronized)) return;

        buffer.writeByte(SyncDataType.MAX_HEALTH)
                .writeInt(destroyable.getGameObjectId())
                .writeShort(destroyable.getMaxHealth());
    }

    @Override
    public synchronized void direction(@NotNull IDirectioned directioned) {
        if (!(directioned instanceof ISynchronized)) return;

        buffer.writeByte(SyncDataType.DIRECTION)
                .writeInt(directioned.getGameObjectId())
                .writeByte(directioned.getDirection() + 1);
    }

    @Override
    public synchronized void aim(@NotNull Actor actor) {
        buffer.writeByte(SyncDataType.AIM)
                .writeInt(actor.getGameObjectId())
                .writeFloat(actor.getAimX())
                .writeFloat(actor.getAimY());
    }

    @Override
    public synchronized void attack(@NotNull Actor actor) {
        buffer.writeByte(SyncDataType.ATTACK)
                .writeInt(actor.getGameObjectId());
    }

    @Override
    public synchronized void visibility(@NotNull IGameObject gameObject, boolean value) {
        if (!(gameObject instanceof ISynchronized)) return;

        buffer.writeByte(SyncDataType.VISIBILITY)
                .writeInt(gameObject.getGameObjectId())
                .writeByte(value ? 1 : 0);
    }

    @Override
    public synchronized void remove(@NotNull IGameObject gameObject) {
        if (!(gameObject instanceof ISynchronized)) return;

        buffer.writeByte(SyncDataType.REMOVE)
                .writeInt(gameObject.getGameObjectId());
    }

    public static synchronized byte[] createSyncMessageOf(IGameObject o) {
        if (!(o instanceof ISynchronized)) return ISyncDataAggregator.EMPTY_ARRAY;
        ISyncDataAggregator aggregator = new SyncDataAggregator();

        aggregator.createSyncDataOf(o);

        if (o.getName().equals("_test_platform_1")) {
            debug("SyncDataAggregator:127: <y><A>" + o.getGameObjectId());
        }

        return aggregator.pullSyncDataMessage();
    }

    @Override
    public synchronized byte[] pullSyncDataMessage() {
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
    public synchronized boolean hasData() {
        return buffer.hasData();
    }
}
