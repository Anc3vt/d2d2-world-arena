/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
