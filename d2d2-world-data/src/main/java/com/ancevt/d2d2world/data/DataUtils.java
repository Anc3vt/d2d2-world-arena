package com.ancevt.d2d2world.data;

public class DataUtils {
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.equals("") || value.equals("null");
    }
}
