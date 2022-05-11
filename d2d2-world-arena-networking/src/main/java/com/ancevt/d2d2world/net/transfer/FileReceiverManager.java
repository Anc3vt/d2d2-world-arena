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
