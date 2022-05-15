
package com.ancevt.d2d2world.net.protocol;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.net.dto.Dto;

public non-sealed interface ClientProtocolImplListener extends ProtocolImplListener {

    default void dtoFromServer(@NotNull Dto extraDto){}

    default void playerPingResponse(){}

    default void fileData(@NotNull String headers, byte[] fileData){}

    default void serverSyncData(byte @NotNull [] syncData){}

    default void playerShoot(int playerId){}
}
