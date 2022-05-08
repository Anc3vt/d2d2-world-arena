/*
 *   D2D2 World Arena Server
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
package com.ancevt.d2d2world.server.player;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.data.file.FileSystemUtils;
import com.ancevt.net.connection.IConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.*;

public class BanList {

    private static final String FILE = "ban.list";

    public static final BanList BANLIST = new BanList();

    private BanList() {
    }

    private @NotNull List<String> loadIps() throws IOException {
        if (FileSystemUtils.exists(FILE)) {
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

    public void ban(@NotNull String addressOrIp) {
        if (addressOrIp.contains("/") && addressOrIp.contains(":")) {
            addressOrIp = IConnection.getIpFromAddress(addressOrIp);
        }

        try {
            if (!loadIps().contains(addressOrIp)) {
                Files.writeString(Path.of(FILE), addressOrIp + "\n", StandardCharsets.UTF_8, CREATE, APPEND, WRITE);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void ban(@NotNull IConnection connection) {
        ban(IConnection.getIpFromAddress(connection.getRemoteAddress()));
        connection.closeIfOpen();
    }

    public void unban(String addressOrIp) {
        if (addressOrIp.contains("/") && addressOrIp.contains(":")) {
            addressOrIp = IConnection.getIpFromAddress(addressOrIp);
        }

        try {
            List<String> list = loadIps();
            list.remove(addressOrIp);
            StringBuilder s = new StringBuilder();
            list.forEach(i -> s.append(i).append('\n'));
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
