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
package com.ancevt.d2d2world.net.transfer;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.data.file.FileSystemUtils;

import static java.lang.Integer.parseInt;
import static com.ancevt.d2d2world.data.GZIP.decompress;
import static com.ancevt.d2d2world.net.transfer.Headers.*;

@Slf4j
public class FileReceiver {

    private final String path;
    private int totalContentLength;
    private int currentContentLength;

    public FileReceiver(@NotNull String path) {
        this.path = path;
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
        if (headers.contains(COMPRESSION)) {
            contentBytes = decompress(contentBytes);
        }

        log.trace("bytesReceived\n{}<contentLength:{}>", headers, contentBytes.length);

        if (headers.contains(ORIGINAL_SIZE)) {
            totalContentLength = parseInt(headers.get(ORIGINAL_SIZE));
        }

        if (headers.contains(UP_TO_DATE)) {
            FileReceiverManager.INSTANCE.wholeFileWritten(this);
            return;
        }

        String pathToAppend = path.startsWith("data/") ? path : "data/" + path;

        if (headers.contains(BEGIN)) {
            FileSystemUtils.truncate(pathToAppend);
        }
        FileSystemUtils.append(pathToAppend, contentBytes);

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
                '}';
    }
}






















