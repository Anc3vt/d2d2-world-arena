/*
 *   D2D2 World Arena Desktop
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
package ru.ancevt.d2d2world.desktop.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class FileDataUtils {

    public static final String DATA = "data/";

    public static File directory(String path) {
        return createDirectoryIfNotExists(path);
    }

    public static boolean exists(String path) {
        return new File(DATA + path).exists();
    }

    public static long getSize(String path) {
        File file = new File(DATA + path);
        if (file.exists()) {
            return file.length();
        } else {
            throw new IllegalStateException("no such file " + DATA + path);
        }
    }

    public static InputStream getInputStream(String path) {
        File file = new File(DATA + path);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException("no such file " + DATA + path);
        }
    }

    private static File createDirectoryIfNotExists(String path) {
        File dir = new File(DATA + path);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                throw new IllegalStateException("unable to create dir " + DATA + path);
            }
        }
        return dir;
    }

    public static void save(String path, byte[] bytes) {
        System.out.println("bytes " + bytes.length);
        try {
            Files.write(Path.of(DATA + path), bytes, WRITE, TRUNCATE_EXISTING, CREATE);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
