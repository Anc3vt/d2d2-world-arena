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
import org.jetbrains.annotations.NotNull;
import ru.ancevt.commons.Holder;
import ru.ancevt.d2d2world.data.DataEntry;
import ru.ancevt.d2d2world.data.file.FileDataUtils;
import ru.ancevt.d2d2world.net.transfer.FileSender;
import ru.ancevt.d2d2world.server.service.GeneralService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Files.newBufferedReader;
import static ru.ancevt.d2d2world.data.DataKey.*;
import static ru.ancevt.d2d2world.server.ServerConfig.CONTENT_COMPRESSION;
import static ru.ancevt.d2d2world.server.ServerConfig.MODULE_SERVER_CONFIG;

@Slf4j
public class ServerContentManager {
    public static final ServerContentManager MODULE_CONTENT_MANAGER = new ServerContentManager();

    private ServerContentManager() {
    }

    public void syncSendFileToPlayer(String path, int playerId) {
        FileSender fileSender = new FileSender(
                path, MODULE_SERVER_CONFIG.getString(CONTENT_COMPRESSION).equals("true"), true
        );
        if (fileSender.isFileExists()) {
            GeneralService.MODULE_GENERAL.getConnection(playerId).ifPresent(fileSender::send);
        }
    }

    public void syncSendMapkit(String mapkitName, int playerId) {
        getMapkits().stream()
                .filter(mapkit -> mapkit.name().equals(mapkitName))
                .findAny()
                .ifPresent(
                        mapkit -> syncSendDirectoryToPlayer("data/mapkits/" + mapkit.uid(), playerId)
                );
    }

    public void syncSendMap(String mapName, int playerId) {
        getMaps().stream()
                .filter(map -> map.name().equals(mapName))
                .findAny()
                .ifPresent(
                        map -> {
                            syncSendFileToPlayer("data/maps/" + map.filename(), playerId);
                            map.mapkits().forEach(mapkit -> {
                                syncSendMapkit(mapkit.name(), playerId);
                            });
                        }
                );
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

    public Set<Map> getMaps() {
        try {
            Set<Map> result = new HashSet<>();

            Set<Path> paths = Files.walk(Paths.get("data/maps/"))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toFile().getName().endsWith(".wam"))
                    .collect(Collectors.toSet());

            paths.forEach(path -> result.add(getMap(path)));

            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Map getMap(Path path) {
        try {
            String name = null;
            long size = 0L;
            Set<Mapkit> mapkits = new HashSet<>();
            String line;
            while ((line = newBufferedReader(path).readLine()) != null) {
                DataEntry dataEntry = DataEntry.newInstance(line);
                if (dataEntry.containsKey(MAP)) {
                    name = dataEntry.getString(NAME);

                    String mapkitUids = dataEntry.getString(MAPKIT_UIDS);

                    for (String mapkitUid : mapkitUids.split(",")) {
                        mapkits.add(getMapkitByIndexFile(Path.of("data/mapkits/" + mapkitUid + "/index.mk")));
                    }

                    size = path.toFile().length();
                    break;
                }
            }

            return new Map(name, FileDataUtils.splitPath(path.toString()).getSecond(), size, mapkits);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public Set<Mapkit> getMapkits() {
        try {
            Set<Mapkit> result = new HashSet<>();

            Set<Path> paths = Files.walk(Paths.get("data/mapkits/"))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toFile().getName().endsWith(".mk"))
                    .collect(Collectors.toSet());

            paths.forEach(path -> result.add(getMapkitByIndexFile(path)));

            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Mapkit getMapkitByIndexFile(Path path) {
        try {
            String name = null;
            String uid = null;

            String line;
            while ((line = newBufferedReader(path).readLine()) != null) {
                DataEntry dataEntry = DataEntry.newInstance(line);
                if (dataEntry.containsKey(MAPKIT)) {
                    name = dataEntry.getString(NAME);
                    uid = dataEntry.getString(UID);
                    break;
                }
            }

            int totalFilesize = countTotalMapkitFilesize(path.toFile().getPath());

            if (uid == null || name == null) {
                throw new IllegalStateException("mapkit uid or name can not be a null (" + uid + "," + name + ")");
            }

            Set<String> files = new HashSet<>();

            Files.walk(Path.of("data/mapkits/" + uid + "/"))
                    .filter(Files::isRegularFile)
                    .forEach(p->files.add(p.getFileName().toString()));

            return new Mapkit(uid, name, totalFilesize, files);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private int countTotalMapkitFilesize(String path) throws IOException {
        Holder<Integer> result = new Holder<>(0);
        Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .forEach(p -> result.setValue(result.getValue() + (int) p.toFile().length()));

        return result.getValue();
    }

    public record Mapkit(@NotNull String uid, @NotNull String name, long totalSize, @NotNull Set<String> files) {
        @Override
        public String toString() {
            return "Mapkit{" +
                    "uid='" + uid + '\'' +
                    ", name='" + name + '\'' +
                    ", totalSize=" + totalSize +
                    ", files=" + files +
                    '}';
        }
    }

    public record Map(@NotNull String name, @NotNull String filename, long size, @NotNull Set<Mapkit> mapkits) {
        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            mapkits.forEach(mapkit -> s.append("\n  ").append(mapkit.toString()));

            return "Map{" +
                    "name='" + name + '\'' +
                    ", filename='" + filename + '\'' +
                    ", size=" + size +
                    ", mapkits=" + s +
                    '}';
        }
    }
}
