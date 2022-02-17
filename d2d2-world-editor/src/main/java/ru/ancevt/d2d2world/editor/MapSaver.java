/*
 *   D2D2 World Editor
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
package ru.ancevt.d2d2world.editor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ancevt.d2d2world.map.GameMap;
import ru.ancevt.d2d2world.map.MapIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MapSaver {

    public static final Logger log = LoggerFactory.getLogger(MapSaver.class);

    public static String mapDirectory;
    public static String mapFileName;

    public static String save(GameMap map, String mapFileName) {
        try {
            log.debug("Saving map " + mapFileName);
            Path path = Path.of(mapDirectory + mapFileName);

            String mapString = MapIO.toString(map);

            Files.writeString(
                    path,
                    mapString,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            log.debug("Saved map " + mapFileName);

            log.debug(mapString);

            return path.toString();
        } catch (IOException e) {
            log.error("Save fault", e);
            throw new IllegalStateException(e);
        }

    }
}
