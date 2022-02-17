/*
 *   D2D2 World Client
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
package ru.ancevt.d2d2world.net.message;

import ru.ancevt.commons.io.ByteInputReader;
import ru.ancevt.d2d2world.net.client.ErrorInfo;

import java.util.Arrays;

public class Message {
    private final byte[] bytes;
    private ByteInputReader byteInputReader;
    private int type;
    private ErrorInfo error;

    private Message(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public boolean isError() {
        return (bytes[0] & 0xff) == MessageType.ERROR;
    }

    public ErrorInfo getErrorInfo() {
        return isError() && error == null ?
                error = new ErrorInfo(
                        inputReader().readShort(),
                        inputReader().readUtf(byte.class),
                        inputReader().hasNextData() ? inputReader().readUtf(int.class) : null
                ) : error;
    }


    public int getType() {
        return bytes[0] & 0xff;
    }

    public ByteInputReader inputReader() {
        if (byteInputReader == null) {
            byteInputReader = ByteInputReader.newInstance(bytes);
            byteInputReader.readByte();
        }
        return byteInputReader;
    }

    public static Message of(byte[] bytes) {
        return new Message(bytes);
    }

    public static String debug(byte[] bytes) {
        StringBuilder s = new StringBuilder();

        int type = bytes[0] & 0xFF;

        s.append(switch (type) {
            case MessageType.CLIENT_RCON_COMMAND -> "REMOTE_CONTROL_COMMAND";
            case MessageType.CLIENT_RCON_LOGIN -> "REMOTE_CONTROL_LOGIN";
            case MessageType.CLIENT_PLAYER_ENTER_REQUEST -> "PLAYER_ENTER_REQUEST";
            case MessageType.CLIENT_PLAYER_EXIT_REQUEST -> "PLAYER_EXIT_REQUEST";
            case MessageType.CLIENT_PLAYER_CONTROLLER_AND_XY_REPORT -> "PLAYER_CONTROLLER_AND_XY_REPORT";
            case MessageType.CLIENT_PLAYER_TEXT_TO_CHAT -> "PLAYER_TEXT_TO_CHAT";
            case MessageType.SERVER_PLAYER_ENTER_RESPONSE -> "PLAYER_ENTER_RESPONSE";
            case MessageType.SERVER_REMOTE_PLAYER_INTRODUCE -> "REMOTE_PLAYER_INTRODUCE";
            case MessageType.SERVER_CHAT -> "REMOTE_PLAYER_CHAT";
            case MessageType.SERVER_REMOTE_PLAYER_CONTROLLER_AND_XY -> "REMOTE_PLAYER_CONTROLLER_AND_XY";
            case MessageType.SERVER_REMOTE_PLAYER_EXIT -> "REMOTE_PLAYER_EXIT";
            case MessageType.ERROR -> "ERROR";
            case MessageType.EXTRA -> "EXTRA";
            default -> "unknown(%d)";
        });

        s.append(String.format("(%d), ", type));

        s.append(Arrays.toString(bytes));

        return s.toString();
    }
}
























