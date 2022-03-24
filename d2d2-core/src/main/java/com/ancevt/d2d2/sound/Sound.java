package com.ancevt.d2d2.sound;

import java.io.InputStream;

public interface Sound {
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
}
