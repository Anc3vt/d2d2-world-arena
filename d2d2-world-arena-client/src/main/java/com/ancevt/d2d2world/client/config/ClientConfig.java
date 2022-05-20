package com.ancevt.d2d2world.client.config;

import com.ancevt.util.config.ListConfig;

public class ClientConfig extends ListConfig {

    public static final String SERVER = "server";
    public static final String PLAYERNAME = "playername";
    public static final String RCON_PASSWORD = "rcon-password";
    public static final String DEBUG_WORLD_ALPHA = "debug.world-alpha";
    public static final String DEBUG_GAME_OBJECT_IDS = "debug.game-object-ids";
    public static final String DEBUG_CHARACTER = "debug.character-mapkit-item";
    public static final String AUTO_ENTER = "auto-enter";
    public static final String DISPLAY_FULLSCREEN = "display.fullscreen";
    public static final String DEBUG_WINDOW_SIZE = "debug.window-size";
    public static final String SOUND_ENABLED = "sound-enabled";
    public static final String DEBUG_WINDOW_XY = "debug.window-xy";
    public static final String DISPLAY_MONITOR = "display.monitor";
    public static final String DISPLAY_RESOLUTION = "display.resolution";

    private static final String PATH = "d2d2-world-arena-client.conf";

    public static final ClientConfig CONFIG = new ClientConfig();

    public ClientConfig() {
        super(PATH);
    }
}
