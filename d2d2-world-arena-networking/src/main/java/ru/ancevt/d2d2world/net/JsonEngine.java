package ru.ancevt.d2d2world.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonEngine {

    private static Gson gson;

    public static Gson gson() {
        if (gson == null) {
            gson = new GsonBuilder().create();
        }

        return gson;
    }
}
