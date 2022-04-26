package com.ancevt.d2d2.fontutils;

import com.ancevt.util.args.Args;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Ttf2D2f {

    public static void main(String[] args) {
        new Ttf2D2f(new Args(args));
    }

    public Ttf2D2f(@NotNull Args args) {
        String inputTtfFile = args.get(String.class, new String[]{"-i", "--input"});
        String outputTtfFile = args.get(String.class, new String[]{"-o", "--output"});

        if (inputTtfFile == null) {
            log.error("--input no specified");
            System.exit(1);
        }
        if (outputTtfFile == null) {
            log.error("--output no specified");
            System.exit(1);
        }
        if (!args.contains("-s", "--size")) {
            log.error("--size not specified");
            System.exit(1);
        }

        int size = args.get(int.class, new String[]{"-s", "--size"});

    }
}
