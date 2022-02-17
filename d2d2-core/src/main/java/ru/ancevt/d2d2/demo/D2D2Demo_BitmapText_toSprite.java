package ru.ancevt.d2d2.demo;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.debug.FpsMeter;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.text.BitmapText;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;

public class D2D2Demo_BitmapText_toSprite {
    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, "D2D2Demo2"));
        Root root = new Root();

        BitmapText bitmapText = new BitmapText();
        bitmapText.setText("""
                    How do I properly load a BufferedImage in java? - Stack ...
                    https://stackoverflow.com › ho...
                    Перевести эту страницу
                    2 сент. 2015 г. — getResource("test.png"); BufferedImage img = (BufferedImage) Toolkit.getDefaultToolkit().getImage(url);. This gives ...
                    2 ответа
                      ·  Лучший ответ: Use ImageIO.read() instead: BufferedImage img = ImageIO.read(url);
                    How to save a BufferedImage as a File - Stack Overflow
                    30 дек. 2017 г.
                    BufferedImage with Transparent PNG - Stack Overflow
                    8 авг. 2017 г.
                    Loading a BufferedImage from a png and preserving ...
                    31 янв. 2018 г.
                    JAVA: How to create "png" image from BufferedImage. Image ...
                    7 сент. 2015 г.
                """);

        Sprite sprite = bitmapText.toSprite();

        sprite.setScale(2,2);
        root.add(sprite, 10, 20);

        D2D2.getTextureManager().bitmapTextToTextureAtlas(bitmapText);

        root.add(new FpsMeter());
        D2D2.getStage().setRoot(root);
        D2D2.loop();
    }
}
