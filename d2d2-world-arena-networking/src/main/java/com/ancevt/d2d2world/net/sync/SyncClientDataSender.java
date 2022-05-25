package com.ancevt.d2d2world.net.sync;

import com.ancevt.d2d2world.gameobject.IDamaging;
import com.ancevt.d2d2world.gameobject.IDestroyable;
import com.ancevt.d2d2world.net.ClientSender;
import com.ancevt.d2d2world.sync.ISyncClientDataSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.ancevt.d2d2world.net.protocol.ClientProtocolImpl.createMessageHealthReport;

public class SyncClientDataSender implements ISyncClientDataSender {

    private final ClientSender clientSender;

    public SyncClientDataSender(ClientSender clientSender) {
        this.clientSender = clientSender;
    }

    @Override
    public synchronized void health(@NotNull IDestroyable destroyable, @Nullable IDamaging damaging) {
        if (damaging == null) {
            clientSender.send(createMessageHealthReport(destroyable.getHealth(), 0));
        } else {
            clientSender.send(createMessageHealthReport(destroyable.getHealth(), damaging.getGameObjectId()));
        }

    }
}
