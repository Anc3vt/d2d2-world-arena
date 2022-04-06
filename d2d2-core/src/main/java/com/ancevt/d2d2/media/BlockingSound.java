package com.ancevt.d2d2.media;

import com.ancevt.commons.concurrent.Async;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.*;

public class BlockingSound implements Media {
    private final ByteArrayOutputStream byteArrayOutputStream;
    private boolean playing;

    @SneakyThrows
    public BlockingSound(@NotNull InputStream inputStream) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(inputStream.readAllBytes());
    }

    @SneakyThrows
    public BlockingSound(String path) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(new FileInputStream(path).readAllBytes());
    }

    public boolean isPlaying() {
        return playing;
    }

    public void stop() {
        playing = false;
    }

    @Override
    public void play() {
        if (!SoundSystem.isEnabled()) return;

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
            BlockingSound sound = null;
                sound = new BlockingSound("/home/ancevt/workspace/ancevt/d2d2/d2d2-world-arena-server/data/mapkits/builtin-mapkit/character-damage.ogg");

            while (true) {
                sound.play();
            }
        });
    }


}
































