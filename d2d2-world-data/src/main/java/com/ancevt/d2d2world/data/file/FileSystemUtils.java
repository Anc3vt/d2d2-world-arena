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
package com.ancevt.d2d2world.data.file;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import com.ancevt.commons.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.ancevt.commons.util.Slash.slashSafe;
import static java.nio.file.StandardOpenOption.*;

public class FileSystemUtils {

    public static @NotNull File directory(@NotNull String path) {
        path = slashSafe(path);
        if (path.endsWith(File.separator)) {
            return createDirectoryIfNotExists(path);
        } else {
            return createDirectoryIfNotExists(splitPath(path).getFirst());
        }
    }

    public static boolean exists(String path) {
        path = slashSafe(path);
        return new File(path).exists();
    }

    public static long getSize(String path) {
        path = slashSafe(path);
        File file = new File(path);
        if (file.exists()) {
            return file.length();
        } else {
            throw new IllegalStateException("no such file " + path);
        }
    }

    @Contract("_ -> new")
    public static @NotNull InputStream getInputStream(String path) {
        path = slashSafe(path);
        File file = new File(path);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException("no such file " + path);
        }
    }

    private static @NotNull File createDirectoryIfNotExists(String path) {
        path = slashSafe(path);
        File dir = new File(path);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                throw new IllegalStateException("unable to create dir " + path);
            }
        }

        return dir;
    }

    public static String readString(String path) {
        path = slashSafe(path);
        try {
            if (!exists(path)) return "";
            return Files.readString(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void save(String path, byte[] bytes) {
        path = path.replace('/', File.separatorChar);
        try {
            directory(path);
            Files.write(Path.of(path), bytes, WRITE, CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void save(String path, String text) {
        path = slashSafe(path);
        try {
            directory(path);
            Files.writeString(Path.of(path), text, WRITE, CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void truncate(String path) {
        save(path, new byte[]{});
    }

    public static void append(String path, byte[] bytes) {
        path = slashSafe(path);
        try {
            directory(path);
            Files.write(Path.of(path), bytes, WRITE, CREATE, APPEND);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static @NotNull Pair<String, String> splitPath(@NotNull String path) {
        path = slashSafe(path);
        String directory = path.substring(0, path.lastIndexOf(File.separatorChar) + 1);
        String filename = path.substring(path.lastIndexOf(File.separatorChar) + 1);
        return Pair.of(directory, filename);
    }

    public static boolean isParent(@NotNull File parent, @NotNull File file) {
        File f;
        try {
            parent = parent.getCanonicalFile();

            f = file.getCanonicalFile();
        } catch (IOException e) {
            return false;
        }

        while (f != null) {
            if (parent.equals(f)) return true;
            f = f.getParentFile();
        }

        return false;
    }
}


















