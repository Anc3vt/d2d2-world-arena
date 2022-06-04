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






















