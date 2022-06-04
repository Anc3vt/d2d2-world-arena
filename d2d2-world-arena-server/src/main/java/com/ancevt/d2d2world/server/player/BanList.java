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
