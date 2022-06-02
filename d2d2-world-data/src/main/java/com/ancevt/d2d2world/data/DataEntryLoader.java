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

package com.ancevt.d2d2world.data;

import com.ancevt.d2d2world.data.file.FileSystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class DataEntryLoader {

    public static DataEntry @NotNull [] load(String path) {
        try {

            final InputStream inputStream = FileSystemUtils.getInputStream(path);
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

            return result.toArray(new DataEntry[0]);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean startsWithWhite(String line) {
        return line.startsWith("\t") || line.startsWith(" ");
    }
}
