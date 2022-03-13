/*
 *   TTF2BMF
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
package com.ancevt.d2d2.ttf2bmf;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

import com.ancevt.util.args.Args;

public class Ttf2Bmf {

    private static final int DEFAULT_FONT_SIZE = 44;
    private static final int DEFAULT_ATLAS_SIZE = 256;

    public static boolean d2d2WorldSpecial;

    public static void main(String[] args) throws IOException {
        Args a = new Args(args);

        if (a.contains("--debug-dev")) {
            a = new Args(new String[]{
                    "--input", "/home/ancevt/.fonts/PressStart2P.ttf",
                    "--output", "PressStart2P.bmf",
                    "-s", "8",
                    "-g"
            });
        }

        d2d2WorldSpecial = a.contains("--d2d2-world-special");

        boolean metaDataOnly = a.contains("--meta-data-only");

        if (a.contains("-v", "--version")) {
            Properties properties = new Properties();
            properties.load(Ttf2Bmf.class.getClassLoader().getResourceAsStream("project.properties"));
            trace(properties.getProperty("project.name") + " " + properties.getProperty("project.version"));
            System.exit(0);
        } else if (a.contains("--help", "-h")) {
            printHelp();
            System.exit(0);
        }

        String inputFilePath = a.get(String.class, new String[]{"--input", "-i"});
        String outputFilePath = a.get(String.class, new String[]{"--output", "-o"});
        boolean guiMode;
        boolean bold;
        boolean italic;
        int atlasWidth = DEFAULT_ATLAS_SIZE;
        int atlasHeight = DEFAULT_ATLAS_SIZE;

        if (inputFilePath == null) {
            error("No input font file ( --input \"fontFile.ttf\" )");
            System.exit(1);
        }

        final File inputFile = new File(inputFilePath);
        if (!inputFile.exists()) {
            error("No such input font file \"" + inputFile.getAbsolutePath() + "\"");
            System.exit(1);
        }

        if (outputFilePath == null) {
            String fileName = inputFile.getName();

            final File outDir = new File("out/");
            if (!outDir.exists()) {
                outDir.mkdir();
            }

            outputFilePath = "out/" + inputFile.getName().substring(0, fileName.indexOf('.')) + ".bmf";
        }

        guiMode = a.contains("--gui", "-g");
        bold = a.contains("--bold", "-B");
        italic = a.contains("--italic", "-I");

        if (a.contains("--atlas-size", "-a")) {
            String atlasSizeString = a.get(String.class, new String[]{"--atlas-size", "-a"});
            String[] splitted = atlasSizeString.split("x");

            atlasWidth = Integer.parseInt(splitted[0]);
            atlasHeight = Integer.parseInt(splitted[1]);
        }

        int fontSize = a.contains("--font-size", "-s") ? a.get(Integer.class, new String[]{"--font-size", "-s"})
                : DEFAULT_FONT_SIZE;

        boolean debugTitleFloating = a.contains("--debug-title-floating", "-F");

        String chars = a.get(String.class, new String[]{"--chars", "-c"},
                "\n !\"#$%&'()*+,-./\\0123456789:;<=>@ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmno" +
                        "pqrstuvwxyz[]_{}АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮ" +
                        "Яабвгдеёжзийклмнопрстуфхцчшщъыьэюя?^~`"
        );

        if(d2d2WorldSpecial) {
            chars = "\n ! \" # $ % & ' ( ) * + , - . / \\ 0 1 2 3 4 5 6 7 8 9 : ; < = > @ A B C D E F G H I J K L M N O P Q R S T U V W X Y Z a b c d e f g h i j k l m n o p q r s t u v w x y z [ ] _ { } А Б В Г Д Е Ё Ж З И Й К Л М Н О П Р С Т У Ф Х Ц Ч Ш Щ Ъ Ы Ь Э Ю Я а б в г д е ё ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я ? ^ ~ ` | ";
        }

        trace("Input: " + inputFilePath);
        trace("Output: " + outputFilePath);
        trace("Font size: " + fontSize);
        trace("Atlas size: " + atlasWidth + "x" + atlasHeight);

        new Ttf2Bmf(
                chars,
                guiMode,
                inputFile,
                outputFilePath,
                fontSize,
                bold,
                italic,
                atlasWidth,
                atlasHeight,
                debugTitleFloating,
                metaDataOnly
        );
    }

    private static void printHelp() {
        trace("Usage:");
        trace("\tjava -jar ttf2bmf.jar --input /path/fontfile.ttf --output /path/fontfile.bmf");
        trace("");
        trace("Additional parameters:");
        trace("\t--help, -h        prints this help page");
        trace("\t--version, -v     prints version of ttf2bmf");
        trace("\t--font-size, -s   font size in points");
        trace("\t--bold, -B        make bold font");
        trace("\t--italic, -I      make italic font");
        trace("\t--atlas-size, -a  size of result atlas (example: -a 44x256)");
        trace("\t--gui, -g         run program in GUI mode (with manual resizing atlas)");
        trace("\t--chars, -c       characters");
    }

    private static void trace(Object o) {
        System.out.println(o == null ? null : o.toString());
    }

    private static void error(Object o) {
        System.err.println(o);
    }

    private final String outputFilePath;
    private final boolean guiMode;

    public Ttf2Bmf(String chars,
                   boolean guiMode,
                   File inputFile,
                   String outputFilePath,
                   int fontSize,
                   boolean bold,
                   boolean italic,
                   int atlasWidth,
                   int atlasHeight,
                   boolean debugTitleFloating,
                   boolean metaDataOnly
    ) {

        this.guiMode = guiMode;

        this.outputFilePath = outputFilePath;

        final Window window = new Window(atlasWidth, atlasHeight) {
            @Override
            public void onRedraw(CharInfo[] charInfos, BufferedImage bufferedImage) {
                Ttf2Bmf.this.onRedraw(charInfos, bufferedImage, metaDataOnly);
            }
        };

        if (guiMode) {
            if (debugTitleFloating) window.setTitle("floating");
            window.setVisible(true);
        }

        String fontName;

        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputFile);
            fontName = font.getName();
            ge.registerFont(font);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }

        final int fontStyle = Font.PLAIN | (bold ? Font.BOLD : Font.PLAIN) | (italic ? Font.ITALIC : Font.PLAIN);

        window.getCanvas().draw(chars, new Font(fontName, fontStyle, fontSize), atlasWidth, atlasHeight);
    }

    private void onRedraw(final CharInfo[] charInfos, final BufferedImage bufferedImage, boolean metaDataOnly) {

        /*
         * BMF file format specification:
         *
         * 1. short: the size of meta info
         * 2. meta info:
         * 		char: char
         * 		short: charX
         * 		short: charY
         * 		short: charWidth
         * 		short: charHeight
         * 			than repeats...
         *
         * 3. PNG-data of atlas
         */

        // Calculate meta data info:

        int metaSize = 0;
        for (CharInfo charInfo : charInfos) {
            if (charInfo != null) {
                metaSize += Character.BYTES;
                metaSize += Short.BYTES * 4;
            }
        }

        System.out.println("metaSize: " + metaSize);

        // Write data

        try {
            final DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(outputFilePath));

            dataOutputStream.writeShort(metaSize);

            System.out.println("metaSize: " + metaSize);

            for (final CharInfo c : charInfos) {
                if (c == null) break;

                dataOutputStream.writeChar(c.character);
                dataOutputStream.writeShort(c.x);
                dataOutputStream.writeShort(c.y);
                dataOutputStream.writeShort(c.width);
                dataOutputStream.writeShort(c.height);

                //System.out.println(c.toString());
            }

            if (!metaDataOnly) {
                ImageIO.write(bufferedImage, "png", dataOutputStream);
            }

            ImageIO.write(bufferedImage, "png", new File("temp.png"));

            dataOutputStream.close();


            try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(outputFilePath))) {
                int test = dataInputStream.readUnsignedShort();
                System.out.println("test: " + test);
            }


            try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(
                    "/home/ancevt/workspace/ancevt/d2d2/d2d2-world-desktop/src/main/resources/assets/bitmapfonts/Terminus_Bold_8x16_spaced_shadowed_v1.bmf"
            ))) {
                int test = dataInputStream.readUnsignedShort();
                System.out.println("test2: " + test);
            }













        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!guiMode) System.exit(0);

    }
}
