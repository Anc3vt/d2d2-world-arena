/*
 *   D2D2 World
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2world.sound;

import com.ancevt.commons.concurrent.Lock;
import com.ancevt.d2d2.media.Media;
import com.ancevt.d2d2.media.SoundSystem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class SoundMachine {

    private static final int DEFAULT_TRACK_COUNT = 16;

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

    public void playAsset(String path, float volume, float pan) {
        if (!SoundSystem.isEnabled()) return;

        Track track = tracks[currentIndex];
        Media media = Media.lookupSoundAsset(path);
        media.setVolume(volume);
        media.setPan(pan);
        track.play(media);
        currentIndex++;
        if (currentIndex >= tracks.length) {
            currentIndex = 0;
        }
    }

    public void play(String path, float volume, float pan) {
        if (!SoundSystem.isEnabled()) return;
        Track track = tracks[currentIndex];
        Media media = Media.lookupSound(path);
        media.setVolume(volume);
        media.setPan(pan);
        track.play(media);
        currentIndex++;
        if (currentIndex >= tracks.length) {
            currentIndex = 0;
        }
    }

    public void stop(String path) {
        if (!SoundSystem.isEnabled()) return;
        Media.lookupSound(path).stop();
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
