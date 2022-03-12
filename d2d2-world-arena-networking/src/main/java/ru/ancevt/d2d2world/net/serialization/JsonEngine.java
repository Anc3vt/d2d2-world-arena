package ru.ancevt.d2d2world.net.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class JsonEngine {

    private static Gson gson;

    public static Gson gson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create();
        }

        return gson;
    }
}

