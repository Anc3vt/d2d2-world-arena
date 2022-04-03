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
package com.ancevt.d2d2world.net.message;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import com.ancevt.commons.io.ByteInputReader;
import com.ancevt.d2d2world.net.client.ErrorInfo;

public class Message {


    public static void main(String[] args) {
        System.out.println(Integer.MAX_VALUE);
    }

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

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Message of(byte[] bytes) {
        return new Message(bytes);
    }

}
