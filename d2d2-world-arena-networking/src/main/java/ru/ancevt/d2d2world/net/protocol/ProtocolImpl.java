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
package ru.ancevt.d2d2world.net.protocol;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.io.ByteOutputWriter;
import ru.ancevt.d2d2world.net.dto.ExtraDto;
import ru.ancevt.d2d2world.net.message.MessageType;

import static ru.ancevt.d2d2world.net.JsonEngine.gson;

public abstract sealed class ProtocolImpl permits ClientProtocolImpl, ServerProtocolImpl  {

    public static final String PROTOCOL_VERSION = "1.0";

    public static byte[] createMessageFileData(@NotNull String headers, byte @NotNull [] fileData) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.FILE_DATA)
                .writeUtf(short.class, headers)
                .writeInt(fileData.length)
                .writeBytes(fileData)
                .toArray();
    }

    public static byte[] createMessageExtra(@NotNull ExtraDto object) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.EXTRA)
                .writeUtf(short.class, object.getClass().getName())
                .writeUtf(int.class, gson().toJson(object))
                .toArray();
    }

    public static byte[] createMessageError(int errorCode, @NotNull String errorMessage, @NotNull String errorDetail) {
        return ByteOutputWriter.newInstance()
                .writeByte(MessageType.ERROR)
                .writeShort(errorCode)
                .writeUtf(byte.class, errorMessage)
                .writeUtf(int.class, errorDetail)
                .toArray();
    }
}
