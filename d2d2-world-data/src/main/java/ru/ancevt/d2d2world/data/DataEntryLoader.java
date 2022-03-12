/*
 *   D2D2 World
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
package ru.ancevt.d2d2world.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class DataEntryLoader {

    private static final Logger log = LoggerFactory.getLogger(DataEntryLoader.class);

    public static DataEntry[] load(String path) throws IOException {
        log.debug("load DataEntry" + path);

        final InputStream inputStream = new FileInputStream(path);
        final List<DataEntry> result = new ArrayList<>();

        try (final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream)
        )) {

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                if (!startsWithWhite(line)) {

                    if (stringBuilder.length() > 0) {
                        //log.debug("creating data entry {}", stringBuilder);
                        result.add(DataEntry.newInstance(stringBuilder.toString()));
                    }
                    stringBuilder.setLength(0);

                    stringBuilder.append(line);

                }

                if (startsWithWhite(line)) {
                    stringBuilder.append(line);
                }
            }

            if (stringBuilder.length() > 0) {
                //log.debug("creating data entry {}", stringBuilder.toString());
                result.add(DataEntry.newInstance(stringBuilder.toString()));
            }
        }

        log.debug("Data entry asset {} loaded", path);

        return result.toArray(new DataEntry[0]);
    }

    private static boolean startsWithWhite(String line) {
        return line.startsWith("\t") || line.startsWith(" ");
    }
}
