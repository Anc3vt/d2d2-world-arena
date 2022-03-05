/*
 *   D2D2 World Arena Server
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
package ru.ancevt.d2d2world.server.content;

import lombok.extern.slf4j.Slf4j;
import ru.ancevt.d2d2world.net.transfer.FileSender;
import ru.ancevt.d2d2world.server.service.GeneralService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Files.newBufferedReader;

@Slf4j
public class ServerContentManager {
    public static final ServerContentManager MODULE_CONTENT_MANAGER = new ServerContentManager();

    private ServerContentManager() {
    }

    public void syncSendFileToPlayer(String path, int playerId) {
        FileSender fileSender = new FileSender(path);
        if (fileSender.isFileExists()) {
            GeneralService.MODULE_GENERAL.getConnection(playerId).ifPresent(fileSender::send);
        }
    }

    public void syncSendDirectoryToPlayer(String path, int playerId) {
        try {

            Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .forEach(p -> {
                        syncSendFileToPlayer(p.toFile().getPath(), playerId);
                    });

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public Set<Mapkit> getMapkits() {
        try {
            Set<Path> paths = Files.walk(Paths.get("data/mapkits/"))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toFile().getName().endsWith(".mk"))
                    .collect(Collectors.toSet());

            paths.forEach(p -> {
                try {
                    String line;
                    while ((line = newBufferedReader(p).readLine()) != null) {

                    }

                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    public record Mapkit(String uid, String name) {
    }
}
