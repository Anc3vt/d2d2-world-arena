
package com.ancevt.d2d2world.net.transfer;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.hash.MD5;
import com.ancevt.d2d2world.data.file.FileSystemUtils;
import com.ancevt.net.connection.IConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.ancevt.d2d2world.data.GZIP.compress;
import static com.ancevt.d2d2world.net.protocol.ProtocolImpl.createMessageFileData;
import static com.ancevt.d2d2world.net.transfer.Headers.*;
import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
public class FileSender {
    public static boolean parentDirectorySecurityEnabled = true;

    public static final int CHUNK_SIZE = 65536;
    public static final int DELAY = 5;

    private final String path;
    private final boolean compress;
    private final boolean hash;

    private CompleteListener completeListener;
    private IConnection connection;
    private int filesize;
    private String hashValue;

    public FileSender(String path, boolean compress, boolean hash) {
        this.hash = hash;
        if (parentDirectorySecurityEnabled && !isSecure(path)) {
            throw new IllegalStateException("security error");
        }

        this.path = path;
        this.compress = compress;
    }

    public void setCompleteListener(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public CompleteListener getCompleteListener() {
        return completeListener;
    }

    public void send(IConnection connection) {
        this.connection = connection;

        if (hash) {
            hashValue = MD5.hashFile(path);
        }

        log.trace("send {} to connection {}", path, connection.getId());

        if (!FileSystemUtils.exists(path)) {
            throw new IllegalStateException("no such file " + path);
        }

        Async.run(this::run);
    }

    private void run() {
        boolean first = true;

        try {
            InputStream inputStream = FileSystemUtils.getInputStream(path);

            filesize = (int) FileSystemUtils.getSize(path);

            int left = filesize;
            while (left > 0) {
                byte[] bytes = new byte[min(left, CHUNK_SIZE)];
                inputStream.read(bytes);
                sendChunk(bytes, first);
                first = false;
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
        if (compress) {
            bytes = compress(bytes);
        }

        Headers headers = newHeaders();

        headers.put(PATH, path)
                .put(ORIGINAL_SIZE, String.valueOf(filesize));

        if (first) {
            headers.put(BEGIN, "true");
            if (hash) {
                headers.put(HASH, hashValue);
            }
        }

        if (compress) {
            headers.put(COMPRESSION, "true");
        }

        connection.send(createMessageFileData(headers.toString(), bytes));
    }

    public boolean isFileExists() {
        return new File(path).exists();
    }

    @FunctionalInterface
    public interface CompleteListener {
        void fileSendComplete();
    }

    public static boolean isSecure(String path) {
        File data = new File("data/");
        return FileSystemUtils.isParent(data, new File(path));
    }

}
