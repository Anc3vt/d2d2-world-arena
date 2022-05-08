/*
 *   D2D2 World Data
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
package com.ancevt.d2d2world.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIP {

    private GZIP() {
    }

    public static byte @NotNull [] compress(byte[] data) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream);
            gzip.write(data);
            gzip.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static byte[] decompress(byte[] data) {
        try {
            InputStream is = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] result = is.readAllBytes();
            is.close();
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static byte @NotNull [] compressUtf(@NotNull String str) {
        return compress(str.getBytes(StandardCharsets.UTF_8));
    }

    @Contract("_ -> new")
    public static @NotNull String decompressUtf(byte[] data) {
        return new String(decompress(data), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String source = "source";

        byte[] p = compress(source.getBytes(StandardCharsets.UTF_8));
        byte[] d = decompress(p);

        System.out.println(new String(d, StandardCharsets.UTF_8));
    }
}
