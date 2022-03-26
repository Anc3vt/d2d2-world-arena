package com.ancevt.d2d2.media;

import com.ancevt.commons.Holder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface Sound {

    Holder<Boolean> enabledHolder = new Holder<>(true);

    static void setEnabled(boolean enabled) {
        enabledHolder.setValue(enabled);
    };

    static boolean isEnabled() {
        return enabledHolder.getValue();
    }

    void setInputStream(InputStream inputStream);

    InputStream getInputStream();

    void setAssetFilePath(String assetFilePath);

    String getAssetFilePath();

    void setLoop(boolean loop);

    boolean isLoop();

    boolean isPlaying();

    void stop();

    void pause();

    void resume();

    void play();

    static void play(String path) {
        try {
            new SoundImpl(new FileInputStream(path)).play();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
