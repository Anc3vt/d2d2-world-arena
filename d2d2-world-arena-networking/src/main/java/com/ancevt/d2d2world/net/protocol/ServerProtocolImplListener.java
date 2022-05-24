
package com.ancevt.d2d2world.net.protocol;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.net.dto.Dto;

public non-sealed interface ServerProtocolImplListener extends ProtocolImplListener {

    default void playerController(int playerId, int controllerState){}

    default void requestFile(int playerId, @NotNull String headers){}

    default void dtoFromPlayer(int playerId, Dto extraDto){}

    default void ping(int playerId){}

    default void playerAimXY(int playerId, float x, float y){}

    default void playerWeaponSwitch(int connectionId, int delta){}

    default void playerHealthReport(int connectionId, int damageValue, int damagingGameObjectId){}

    default void playerXY(int connectionId, float x, float y){}

    default void playerHook(int connectionId, int hookGameObjectId) {}
}
