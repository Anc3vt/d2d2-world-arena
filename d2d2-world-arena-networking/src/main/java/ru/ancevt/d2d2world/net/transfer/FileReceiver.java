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
package ru.ancevt.d2d2world.net.transfer;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.data.file.FileDataUtils;

import static java.lang.Integer.parseInt;
import static ru.ancevt.d2d2world.data.GZIP.decompress;
import static ru.ancevt.d2d2world.net.transfer.Headers.BEGIN;
import static ru.ancevt.d2d2world.net.transfer.Headers.SIZE;

@Slf4j
public class FileReceiver {

    private final String path;
    private final boolean compressed;
    private int totalContentLength;
    private int currentContentLength;

    public FileReceiver(@NotNull String path, boolean compressed) {
        this.path = path;
        this.compressed = compressed;
    }

    public String getPath() {
        return path;
    }

    public int bytesTotal() {
        return totalContentLength;
    }

    public int bytesLoaded() {
        return currentContentLength;
    }

    public void bytesReceived(@NotNull Headers headers, byte @NotNull [] contentBytes) {
        if(compressed) {
            contentBytes = decompress(contentBytes);
        }

        log.trace("bytesReceived\n{}<contentLength:{}>", headers, contentBytes.length);

        if (headers.contains(SIZE)) {
            totalContentLength = parseInt(headers.get(SIZE));
        }

        String pathToAppend = path.startsWith("data/") ? path : "data/" + path;

        if (headers.contains(BEGIN)) {
            FileDataUtils.truncate(pathToAppend);
        }
        FileDataUtils.append(pathToAppend, contentBytes);

        currentContentLength += contentBytes.length;
        FileReceiverManager.INSTANCE.progress(this);
        if (currentContentLength >= totalContentLength) {
            FileReceiverManager.INSTANCE.wholeFileWritten(this);
        }
    }

    @Override
    public String toString() {
        return "FileReceiver{" +
                "path='" + path + '\'' +
                ", bytesTotal=" + totalContentLength +
                ", bytesLoaded=" + currentContentLength +
                ", compressed=" + compressed +
                '}';
    }
}






















