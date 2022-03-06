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
package ru.ancevt.d2d2world.data.file;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

public class FileDataUtils {

    public static @NotNull File directory(@NotNull String path) {
        if (path.endsWith(File.separator)) {
            return createDirectoryIfNotExists(path);
        } else {
            return createDirectoryIfNotExists(splitPath(path).getFirst());
        }
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static long getSize(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.length();
        } else {
            throw new IllegalStateException("no such file " + path);
        }
    }

    @Contract("_ -> new")
    public static @NotNull InputStream getInputStream(String path) {
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
        try {
            if (!exists(path)) return "";
            return Files.readString(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void save(String path, byte[] bytes) {
        try {
            directory(path);
            Files.write(Path.of(path), bytes, WRITE, CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void truncate(String path) {
        save(path, new byte[]{});
    }

    public static void append(String path, byte[] bytes) {
        try {
            directory(path);
            Files.write(Path.of(path), bytes, WRITE, CREATE, APPEND);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static @NotNull Pair<String, String> splitPath(@NotNull String path) {
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


















