package com.ancevt.d2d2world.server.player;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.data.file.FileDataUtils;
import com.ancevt.net.connection.IConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.*;

public class BanList {

    private static final String FILE = "ban.list";

    public static final BanList MODULE_BANLIST = new BanList();

    private BanList() {
    }

    private @NotNull List<String> loadIps() throws IOException {
        if (FileDataUtils.exists(FILE)) {
            return Files.readAllLines(Path.of(FILE), StandardCharsets.UTF_8);
        } else {
            return List.of();
        }
    }

    public boolean isBanned(String ip) {
        try {
            return loadIps().contains(ip);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean ifBannedCloseConnection(@NotNull IConnection connection) {
        boolean result = isBanned(IConnection.getIpFromAddress(connection.getRemoteAddress()));
        if (result) {
            connection.closeIfOpen();
        }
        return result;
    }

    public void ban(String ip) {
        try {
            if (!loadIps().contains(ip)) {
                Files.writeString(Path.of(FILE), ip + "\n", StandardCharsets.UTF_8, CREATE, APPEND, WRITE);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void ban(IConnection connection) {
        ban(IConnection.getIpFromAddress(connection.getRemoteAddress()));
        connection.closeIfOpen();
    }

    public void unban(String ip) {
        try {
            List<String> list = loadIps();
            list.remove(ip);
            StringBuilder s = new StringBuilder();
            list.forEach(i->s.append(i).append('\n'));
            Files.writeString(Path.of(FILE), s.toString(), StandardCharsets.UTF_8, CREATE, WRITE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<String> getList() {
        try {
            return loadIps();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
