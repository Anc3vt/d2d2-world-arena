package com.ancevt.d2d2.media;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.concurrent.Lock;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static com.ancevt.commons.unix.UnixDisplay.debug;

public class SoundImpl2 implements Media {
    private final ByteArrayOutputStream byteArrayOutputStream;
    private boolean playing;
    private boolean loop;
    private Thread thread;
    private float pan = 0f;
    private float volume = 1f;

    private static int counter = 0;

    @SneakyThrows
    public SoundImpl2(@NotNull InputStream inputStream) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(inputStream.readAllBytes());
    }

    public SoundImpl2(String path) throws FileNotFoundException {
        this(new FileInputStream(path));
    }

    @Override
    public void setVolume(float volume) {
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

    public void stop() {
        playing = false;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public void play() {
        if (!SoundSystem.isEnabled()) return;

        if (isPlaying()) stop();

        if (thread == null) {
            thread = new Thread(() -> {

                /*
                counter++;
                if (counter > MAX_SOUNDS) {
                    while (counter > MAX_SOUNDS) {
                        new Lock().lock(10, TimeUnit.MILLISECONDS);
                    }
                }

                 */

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

                } while (playing && isLoop());

                playing = false;
                thread = null;
                counter--;

            });

            playing = true;
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    private boolean isLoop() {
        return false;
    }

    @Contract(" -> new")
    private @NotNull InputStream read() {
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
            SoundImpl2 sound = null;
            try {
                sound = new SoundImpl2(new FileInputStream("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/builtin-mapkit/character-damage.ogg"));
                sound.setPan(1f);
                sound.setVolume(2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (true) {
                sound.play();
                sound.setVolume(sound.getVolume() - 1);
                debug("SoundImpl2:185: <A>" + sound.getVolume());
                sound.setPan(sound.getPan() - 0.01f);
                new Lock().lock(100, TimeUnit.MILLISECONDS);
            }
        });
    }


}
































