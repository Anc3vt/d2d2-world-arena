
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
