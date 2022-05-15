
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
