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

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.net.file.FileDataUtils;

import static java.lang.Integer.parseInt;
import static ru.ancevt.d2d2world.net.transfer.Headers.SIZE;

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
        if (headers.contains(SIZE)) {
            totalContentLength = parseInt(headers.get(SIZE));
        }

        //if(headers.contains(BEGIN)) {
        //    FileDataUtils.truncate("data/" + path);
        // }

        FileDataUtils.append("data/" + path, contentBytes);

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






















