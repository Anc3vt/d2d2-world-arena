package com.ancevt.d2d2world.desktop.sound;

import com.ancevt.d2d2.media.SoundMachine;

public class D2D2WorldSound {

    public final static String PLAYER_ENTER = "sound/d2d2world/player-enter.ogg";
    public final static String PLAYER_EXIT = "sound/d2d2world/player-exit.ogg";
    public final static String PLAYER_SPAWN = "sound/d2d2world/player-spawn.ogg";

    public static void playSound(String d2d2WorldSound) {
        SoundMachine.getInstance().playAsset(d2d2WorldSound);
    }
}
