/*
 *   D2D2 World Arena Networking
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2world.net.protocol;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.net.dto.Dto;

public non-sealed interface ClientProtocolImplListener extends ProtocolImplListener {

    void dtoFromServer(@NotNull Dto extraDto);

    void playerPingResponse();

    void fileData(@NotNull String headers, byte[] fileData);

    void serverSyncData(byte @NotNull [] syncData);
}
