package com.ancevt.d2d2world.client.storage;

import com.ancevt.d2d2world.client.D2D2WorldArenaClientMain;
import com.ancevt.localstorage.FileLocalStorage;
import com.ancevt.localstorage.LocalStorage;
import com.ancevt.localstorage.LocalStorageBuilder;

public class LocalStorageManager {

    public static final String D2D2WORLD_CLIENT_INTRO_PLAYERNAME = "d2d2world.client.intro.playername";
    public static final String D2D2WORLD_CLIENT_INTRO_PASSWORD = "d2d2world.client.intro.password";
    public static final String D2D2WORLD_CLIENT_INTRO_SERVERLIST = "d2d2world.client.intro.serverlist";
    public static final String D2D2WORLD_CLIENT_INTRO_CURRENTSERVER = "d2d2world.client.intro.currentserver";
    public static final String D2D2WORLD_CLIENT_DISPLAY_CURRENTMONITOR = "d2d2world.client.display.currentmonitor";
    public static final String D2D2WORLD_CLIENT_DISPLAY_RESOLUTION = "d2d2world.client.display.resolution";
    public static final String D2D2WORLD_CLIENT_DISPLAY_FULLSCREEN = "d2d2world.client.display.fullscreen";
    public static final String D2D2WORLD_CLIENT_DEBUG_ENABLED = "d2d2world.client.debug.enabled";
    public static final String D2D2WORLD_CLIENT_DEBUG_WINDOWXY = "d2d2world.client.debug.windowxy";
    public static final String D2D2WORLD_CLIENT_DEBUG_WINDOWSIZE = "d2d2world.client.debug.windowsize";
    public static final String D2D2WORLD_CLIENT_DEBUG_SCENE_GAMEOBJECTIDSVISIBLE = "d2d2world.client.debug.gameobjectidsvisible";
    public static final String D2D2WORLD_CLIENT_DEBUG_CHARACTERMAPKITITEM = "d2d2world.client.debug.charactermapkititem";

    private static final String STORAGE_FILE_NAME = "storage";
    private static LocalStorage localStorage;

    public static LocalStorage localStorage() {
        if (localStorage == null)
            throw new IllegalStateException("Local storage is not initialized yet. Call init() first.");

        return localStorage;
    }

    public static void init() {
        if (localStorage != null)
            throw new IllegalStateException("Local storage is already initialized");

        localStorage = new LocalStorageBuilder(STORAGE_FILE_NAME, FileLocalStorage.class)
                .saveOnWrite(false)
                .storageId(D2D2WorldArenaClientMain.class.getPackageName())
                .build();

        applyDefaults();
    }

    private static void applyDefaults() {
        localStorage.computeIfAbsent(D2D2WORLD_CLIENT_INTRO_PLAYERNAME, k -> "");
        localStorage.computeIfAbsent(D2D2WORLD_CLIENT_INTRO_PASSWORD, k -> "");
    }
}
