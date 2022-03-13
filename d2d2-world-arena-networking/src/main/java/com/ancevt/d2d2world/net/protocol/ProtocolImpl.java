/*
 *   D2D2 World Arena Networking
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import com.ancevt.commons.io.ByteOutputWriter;
import com.ancevt.d2d2world.net.message.MessageType;

@Slf4j
public abstract sealed class ProtocolImpl permits ClientProtocolImpl, ServerProtocolImpl {

    public static final String PROTOCOL_VERSION = "1.0";

    @Contract(value = " -> new", pure = true)
    public static byte @NotNull [] createMessagePing() {
        return new byte[]{(byte) MessageType.PING};
    }

    public static byte[] createMessageFileData(@NotNull String headers, byte @NotNull [] fileData) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.FILE_DATA)
                .writeUtf(short.class, headers)
                .writeInt(fileData.length)
                .writeBytes(fileData)
                .toByteArray();
    }

    public static byte[] createDtoMessage(@NotNull String className, @NotNull String json) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.DTO)
                .writeUtf(short.class, className)
                .writeUtf(int.class, json)
                .toByteArray();
    }

}
