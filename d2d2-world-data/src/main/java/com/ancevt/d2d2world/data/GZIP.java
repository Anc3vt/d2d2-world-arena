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
