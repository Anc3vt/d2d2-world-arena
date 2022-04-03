/*
 *   D2D2 core
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
package com.ancevt.d2d2.media;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.d2d2.asset.Assets;
import com.ancevt.d2d2.exception.NotImplementedException;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.TimeUnit;


public class SoundImpl implements Sound {

    private static int counter = 0;

    private InputStream inputStream;
    private ByteArrayInputStream byteArrayInputStream;
    private String assetFilePath;
    private boolean loop;
    private volatile boolean playing;
    private Thread thread;

    public SoundImpl() {

    }

    public SoundImpl(String assetFilePath) {
        setAssetFilePath(assetFilePath);
    }

    public SoundImpl(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream getInputStream() {
        return read();
    }

    @Override
    public void setAssetFilePath(String assetFilePath) {
        if (isPlaying()) {
            throw new IllegalStateException("sound is playing now");
        }
        this.assetFilePath = assetFilePath;
    }

    @Override
    public String getAssetFilePath() {
        return assetFilePath;
    }

    @Override
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    @Override
    public boolean isLoop() {
        return loop;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void stop() {
        playing = false;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public void pause() {
        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public void resume() {
        // TODO: implement
        throw new NotImplementedException();
    }

    @Override
    public void play() {

        if (!Sound.isEnabled()) return;

        if (inputStream == null && assetFilePath == null) {
            throw new IllegalStateException("No inputStream or assetFilePath of sound to play");
        }

        if (isPlaying()) stop();

        counter ++;
        if(counter > MAX_SOUNDS) {
            while(counter > MAX_SOUNDS) {
                new Lock().lock(10, TimeUnit.MILLISECONDS);
            }
        }

        if (thread == null) {
            thread = new Thread(() -> {

                do {
                    try (final AudioInputStream in = AudioSystem.getAudioInputStream(read())) {

                        final AudioFormat outFormat = getOutFormat(in.getFormat());
                        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);

                        try (final SourceDataLine line =
                                     (SourceDataLine) AudioSystem.getLine(info)) {

                            if (line != null) {
                                line.open(outFormat);
                                line.start();
                                stream(AudioSystem.getAudioInputStream(outFormat, in), line);
                                line.drain();
                                line.stop();
                            }
                        }


                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                        throw new IllegalStateException(e);
                    }

                } while (isLoop() && playing);

                playing = false;
                thread = null;
                counter --;

            });

            playing = true;
            thread.setDaemon(true);
            thread.start();
        }
    }

    private InputStream read() {
        try {
            if (inputStream == null) {
                inputStream = Assets.getAssetAsStream(assetFilePath);
            }
            if (byteArrayInputStream == null) {
                byteArrayInputStream = new ByteArrayInputStream(inputStream.readAllBytes());
                byteArrayInputStream.mark(0);
            }
            byteArrayInputStream.reset();

            return byteArrayInputStream;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private @NotNull AudioFormat getOutFormat(@NotNull AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
            if (!playing) {
                break;
            }
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        //SoundImpl sound = new SoundImpl("sound/tap.ogg");


        Async.run(() -> {
            while (true) {
                SoundImpl sound = null;
                try {
                    sound = new SoundImpl(new FileInputStream("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/builtin-mapkit/character-damage.ogg"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                sound.play();
                new Lock().lock(1000, TimeUnit.MILLISECONDS);
            }
        });
    }

}
