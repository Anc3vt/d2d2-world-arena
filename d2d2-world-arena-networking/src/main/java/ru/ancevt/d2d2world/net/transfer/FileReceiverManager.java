package ru.ancevt.d2d2world.net.transfer;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.io.ByteInputReader;
import ru.ancevt.d2d2world.net.message.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.ancevt.d2d2world.net.transfer.Headers.PATH;

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

    public void fileData(byte[] fullMessageBytes) {
        ByteInputReader in = Message.of(fullMessageBytes).inputReader();
        Headers headers = Headers.of(in.readUtf(short.class));
        int contentLength = in.readInt();
        byte[] fileData = in.readBytes(contentLength);
        fileData(headers, fileData);
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
        fileReceiverManagerListeners.forEach(l -> l.complete(fileReceiver));
    }

    public void progress(FileReceiver fileReceiver) {
        fileReceiverManagerListeners.forEach(l -> l.progress(fileReceiver));
    }

    public interface FileReceiverManagerListener {
        void progress(FileReceiver fileReceiver);

        void complete(FileReceiver fileReceiver);
    }
}
