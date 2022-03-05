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
import ru.ancevt.commons.concurrent.Async;
import ru.ancevt.net.tcpb254.connection.IConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static ru.ancevt.d2d2world.net.protocol.ProtocolImpl.createMessageFileData;
import static ru.ancevt.d2d2world.net.transfer.Headers.newHeaders;

@Slf4j
public class FileSender {
    public static final int CHUNK_SIZE = 4096;
    public static final int DELAY = 10;

    private final String path;
    private File file;

    private CompleteListener completeListener;
    private IConnection connection;
    private int filesize;

    public FileSender(String path) {
        this.path = path;
    }

    public void setCompleteListener(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public CompleteListener getCompleteListener() {
        return completeListener;
    }

    public void send(IConnection connection) {
        this.connection = connection;
        file = new File(path);

        System.out.println(path);

        if (!file.exists()) {
            throw new IllegalStateException("no such file " + file);
        }

        Async.run(this::run);
    }

    private void run() {
        boolean first = true;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            filesize = (int) file.length();
            int left = filesize;
            while (left > 0) {
                byte[] bytes = new byte[min(left, CHUNK_SIZE)];
                fileInputStream.read(bytes);
                sendChunk(bytes, first);
                left -= bytes.length;

                Async.wait(DELAY, MILLISECONDS);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        if (completeListener != null) {
            completeListener.fileSendComplete();
        }
    }

    private void sendChunk(byte[] bytes, boolean first) {
        if (first) {
            connection.send(createMessageFileData(
                    newHeaders()
                            .put(Headers.PATH, path)
                            .put(Headers.BEGIN, "true")
                            .put(Headers.SIZE, String.valueOf(filesize))
                            .toString(),
                    bytes)
            );
        } else {
            connection.send(createMessageFileData(
                    newHeaders()
                            .put(Headers.PATH, path)
                            .put(Headers.SIZE, String.valueOf(filesize))
                            .toString(),
                    bytes)
            );
        }
    }

    @FunctionalInterface
    public interface CompleteListener {
        void fileSendComplete();
    }
}





















