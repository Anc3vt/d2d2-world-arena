/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
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
package com.ancevt.d2d2.sound;

import com.ancevt.d2d2.asset.Assets;
import com.ancevt.d2d2.exception.NotImplementedException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class Sound {

    private static final String SOUND_ASSET_DIR = "sound/";

    public static void main(String[] args) {
        new Sound("sound/jump.mp3").play();
    }

    private String assetFilePath;
    private boolean loop;
    private boolean playing;
    private Thread thread;

    public Sound() {

    }

    public Sound(String assetFilePath) {
        this();
        setAssetFilePath(assetFilePath);
    }

    public void setAssetFilePath(String assetFilePath) {
        if (isPlaying()) {
            throw new IllegalStateException("sound is playing now");
        }
        this.assetFilePath = assetFilePath;
    }

    public String getAssetFilePath() {
        return assetFilePath;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void stop() {
        thread.interrupt();
        playing = false;
    }

    public void pause() {
        // TODO: implement
        throw new NotImplementedException();
    }

    public void resume() {
        // TODO: implement
        throw new NotImplementedException();
    }

    public void play() {

        if (thread == null) {
            thread = new Thread(() -> {

                do {
                    try (final AudioInputStream in = getAudioInputStream(Assets.getAssetAsStream(SOUND_ASSET_DIR + assetFilePath))) {

                        final AudioFormat outFormat = getOutFormat(in.getFormat());
                        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);

                        try (final SourceDataLine line =
                                     (SourceDataLine) AudioSystem.getLine(info)) {

                            if (line != null) {
                                line.open(outFormat);
                                line.start();
                                stream(getAudioInputStream(outFormat, in), line);
                                line.drain();
                                line.stop();
                            }
                        }

                    } catch (UnsupportedAudioFileException
                            | LineUnavailableException
                            | IOException e) {
                        throw new IllegalStateException(e);
                    }

                    if (isLoop()) {
                        play();
                    }
                } while (isLoop());

                playing = false;
                thread = null;
            });

            playing = true;
            thread.start();
        }
    }

    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();

        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }

}
