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
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.*;

import static com.ancevt.commons.util.Slash.slashSafe;

public class BlockingSound implements Media {
    private final ByteArrayOutputStream byteArrayOutputStream;
    private boolean playing;

    private float volume = 1f;
    private float pan = 0f;

    @SneakyThrows
    public BlockingSound(@NotNull InputStream inputStream) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(inputStream.readAllBytes());
    }

    @SneakyThrows
    public BlockingSound(String path) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(new FileInputStream(slashSafe(path)).readAllBytes());
    }

    @Override
    public void setVolume(float volume) {
        if(volume < -80f) volume = -80f;
        if(volume >= 6f) volume = 6f;
        this.volume = volume;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setPan(float pan) {
        if (pan < -1f) {
            pan = -1f;
        } else if (pan > 1f) {
            pan = 1f;
        }
        this.pan = pan;
    }

    @Override
    public float getPan() {
        return pan;
    }

    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void stop() {
        playing = false;
    }

    @Override
    public void play() {
        if (!SoundSystem.isEnabled() || volume < -15f) return;

        if (isPlaying()) stop();

        playing = true;

        try (final AudioInputStream in = AudioSystem.getAudioInputStream(getInputStream())) {
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
    }

    @Contract(" -> new")
    private @NotNull InputStream getInputStream() {
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private @NotNull AudioFormat getOutFormat(@NotNull AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            FloatControl floatControlPan = (FloatControl) line.getControl(FloatControl.Type.PAN);
            floatControlPan.setValue(pan);
            FloatControl floatControlVolume = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            floatControlVolume.setValue(volume);
            line.write(buffer, 0, n);
            if (!playing) break;
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        //SoundImpl sound = new SoundImpl("sound/tap.ogg");

        Async.run(() -> {
            BlockingSound sound = null;
            sound = new BlockingSound("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/builtin-mapkit/character-damage.ogg");
            while (true) {
                sound.play();
            }
        });
    }
}

