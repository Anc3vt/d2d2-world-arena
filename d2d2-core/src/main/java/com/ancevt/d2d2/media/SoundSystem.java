package com.ancevt.d2d2.media;

public class SoundSystem {

    private static boolean enabled = true;

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}