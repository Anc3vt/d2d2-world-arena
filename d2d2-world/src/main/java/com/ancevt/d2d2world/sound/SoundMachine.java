/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
