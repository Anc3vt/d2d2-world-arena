package com.ancevt.d2d2.media;

import com.ancevt.commons.concurrent.Lock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class SoundMachine {

    private static final int DEFAULT_TRACK_COUNT = 8;

    private static SoundMachine instance;

    public static SoundMachine getInstance() {
        return instance == null ? instance = new SoundMachine(DEFAULT_TRACK_COUNT) : instance;
    }

    public final Track[] tracks;
    private int currentIndex = 0;

    public SoundMachine(int trackCount) {
        tracks = new Track[trackCount];
        for (int i = 0; i < tracks.length; i++) {
            Track track = new Track("sndtrk" + i);
            track.start();
            tracks[i] = track;
        }
    }

    public void playAsset(String path) {
        if (!SoundSystem.isEnabled()) return;

        Track track = tracks[currentIndex];
        track.play(Media.createSoundAsset(path));
        currentIndex++;
        if (currentIndex >= tracks.length) {
            currentIndex = 0;
        }
    }

    public void play(String path) {
        if (!SoundSystem.isEnabled()) return;

        Track track = tracks[currentIndex];
        track.play(Media.createSound(path));
        currentIndex++;
        if (currentIndex >= tracks.length) {
            currentIndex = 0;
        }
    }

    private static class Track extends Thread {

        private boolean alive;
        private final Lock lock;
        private final Queue<Media> queue;

        public Track(String name) {
            setName(name);
            setDaemon(true);
            alive = true;
            lock = new Lock();
            queue = new LinkedBlockingQueue<>();
        }

        public void dispose() {
            alive = false;
            lock.unlockIfLocked();
        }

        public synchronized void play(@NotNull Media media) {
            queue.add(media);
            lock.unlockIfLocked();
        }

        @Override
        public void run() {
            while (alive) {
                lock.lock();
                while (!queue.isEmpty()) {
                    queue.poll().play();
                }
            }
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "Track{" +
                    "name=" + getName() +
                    ", alive=" + alive +
                    '}';
        }
    }
}
