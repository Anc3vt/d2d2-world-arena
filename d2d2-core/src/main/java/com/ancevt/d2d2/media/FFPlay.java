package com.ancevt.d2d2.media;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.io.ByteOutputWriter;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.nio.file.StandardOpenOption.*;

public class FFPlay {

    private static final String DIR = "ffplay/";
    private static String executableFile;
    private static boolean isWindows;

    public static void init() {
        prepareExecutive();
    }

    private static String osName() {
        return System.getProperty("os.name").toLowerCase();
    }

    private static void prepareExecutive() {
        isWindows = osName().startsWith("windows");

        if (isWindows) {
            executableFile = "ffplay.exe";
        } else {
            executableFile = "ffplay";
        }

        File file = new File(DIR + executableFile);
        if (file.exists()) {
            file.delete();
        } else {
            if (!new File(DIR).exists()) {
                new File(DIR).mkdir();
            }
        }

        InputStream inputStream = FFPlay.class.getClassLoader().getResourceAsStream(DIR + executableFile);
        try {
            if (inputStream != null) {
                var bow = ByteOutputWriter.newInstance().writeBytes(inputStream.readAllBytes());
                Files.write(file.toPath(), bow.toByteArray(), CREATE, WRITE, TRUNCATE_EXISTING);
            } else {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        file.setExecutable(true);
        file.deleteOnExit();
    }

    public static void play(String filePath) {
        Async.run(() -> {
            try {
                ProcessBuilder builder = new ProcessBuilder();
                if (isWindows) {
                    builder.command("cmd.exe", "/c", DIR + executableFile, filePath);
                } else {
                    builder.command(DIR + executableFile, filePath, "-nodisp", "-autoexit");
                }
                //builder.directory(new File(System.getProperty("user.home")));
                Process process = null;

                process = builder.start();

                StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
                Executors.newSingleThreadExecutor().submit(streamGobbler);
                int exitCode = process.waitFor();
                assert exitCode == 0;
            } catch (IOException | InterruptedException e) {
                throw new IllegalStateException(e);
            }
        });
    }


    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }


    public static void main(String[] args) {
        System.out.println(osName());
        prepareExecutive();
        for (int i = 0; i < 100; i++) {
            long o = System.currentTimeMillis();
            play("src/main/resources/assets/sound/tap.ogg");
            System.out.println(System.currentTimeMillis() - o);
        }
    }
}
