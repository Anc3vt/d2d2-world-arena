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

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ancevt.d2d2world.net.transfer.Headers.PATH;

public class FileReceiverManager {

    public static final FileReceiverManager INSTANCE = new FileReceiverManager();

    private final Map<String, FileReceiver> fileReceiverMap;

    private final List<FileReceiverManagerListener> fileReceiverManagerListeners;

    private FileReceiverManager() {
        fileReceiverMap = new ConcurrentHashMap<>();
        fileReceiverManagerListeners = new CopyOnWriteArrayList<>();
    }

    public void addFileReceiverManagerListener(FileReceiverManagerListener l) {
        fileReceiverManagerListeners.add(l);
    }

    public void removeFileReceiverManagerListener(FileReceiverManagerListener l) {
        fileReceiverManagerListeners.remove(l);
    }

    public void fileData(@NotNull Headers headers, byte @NotNull [] fileData) {
        getFileReceiver(headers.get(PATH)).bytesReceived(headers, fileData);
    }

    private FileReceiver getFileReceiver(String path) {
        if (fileReceiverMap.containsKey(path)) {
            return fileReceiverMap.get(path);
        } else {
            FileReceiver fileReceiver = new FileReceiver(path);
            fileReceiverMap.put(path, fileReceiver);
            return fileReceiver;
        }
    }

    public void wholeFileWritten(@NotNull FileReceiver fileReceiver) {
        fileReceiverMap.remove(fileReceiver.getPath());
        fileReceiverManagerListeners.forEach(l -> l.fileReceiverComplete(fileReceiver));
    }

    public void progress(FileReceiver fileReceiver) {
        fileReceiverManagerListeners.forEach(l -> l.fileReceiverProgress(fileReceiver));
    }

    public interface FileReceiverManagerListener {
        void fileReceiverProgress(FileReceiver fileReceiver);

        void fileReceiverComplete(FileReceiver fileReceiver);
    }
}
