package com.ancevt.d2d2.sound;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.d2d2.exception.NotImplementedException;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SoundPool implements Sound {

    private static final int DEFAULT_CAPACITY = 50;

    private final int capacity;
    private InputStream inputStream;

    private final List<Sound> sounds;
    private int currentIndex;

    public SoundPool(InputStream inputStream) {
        this(inputStream, DEFAULT_CAPACITY);
    }

    public SoundPool() {
        this(DEFAULT_CAPACITY);
    }

    public SoundPool(String assetFilePath) {
        this(assetFilePath, DEFAULT_CAPACITY);
    }

    public SoundPool(InputStream inputStream, int capacity) {
        if (capacity == 0) throw new IllegalStateException("capacity must be > 0");
        this.capacity = capacity;
        sounds = new ArrayList<>(capacity);
        setInputStream(inputStream);
    }

    public SoundPool(String assetFilePath, int capacity) {
        if (capacity == 0) throw new IllegalStateException("capacity must be > 0");
        this.capacity = capacity;
        sounds = new ArrayList<>(capacity);
        setAssetFilePath(assetFilePath);
    }

    public SoundPool(int capacity) {
        if (capacity == 0) throw new IllegalStateException("capacity must be > 0");
        this.capacity = capacity;
        sounds = new ArrayList<>(capacity);
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        for (int i = 0; i < capacity; i++) {
            Sound sound = new SoundImpl(inputStream);
            sound.getInputStream();
            sounds.add(sound);
        }
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void setAssetFilePath(String assetFilePath) {
        for (int i = 0; i < capacity; i++) {
            Sound sound = new SoundImpl(assetFilePath);
            sound.getInputStream();
            sounds.add(sound);
        }

        this.inputStream = sounds.get(0).getInputStream();
    }

    @Override
    public String getAssetFilePath() {
        return sounds.get(0).getAssetFilePath();
    }

    @Override
    public void setLoop(boolean loop) {
        sounds.forEach(s -> s.setLoop(loop));
    }

    @Override
    public boolean isLoop() {
        return sounds.get(0).isLoop();
    }

    @Override
    public boolean isPlaying() {
        return sounds.stream().anyMatch(Sound::isPlaying);
    }

    @Override
    public void stop() {
        sounds.forEach(Sound::stop);
    }

    @Override
    public void pause() {
        throw new NotImplementedException();
    }

    @Override
    public void resume() {
        throw new NotImplementedException();
    }

    @Override
    public void play() {
        if(Sound.isEnabled()) {
            getNextSound().play();
        }
    }

    private Sound getNextSound() {
        Sound currentSound = sounds.get(currentIndex);
        currentIndex++;
        if(currentIndex >= sounds.size()) {
            currentIndex = 0;
        }
        return currentSound;
    }

    @SneakyThrows
    public static void main(String[] args) {
        Sound.setEnabled(true);
        //Sound sound = new SoundPool("sound/tap.ogg", 50);
        var sound = new SoundImpl(new FileInputStream("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/character-mapkit/lazer.ogg"));
        Async.run(() -> {
            while (true) {
                sound.play();
                new Lock().lock(250, TimeUnit.MILLISECONDS);
            }
        });
    }
}




























