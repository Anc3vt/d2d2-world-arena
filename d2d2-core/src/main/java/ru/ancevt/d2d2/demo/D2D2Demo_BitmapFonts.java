package ru.ancevt.d2d2.demo;

import ru.ancevt.d2d2.D2D2;
import ru.ancevt.d2d2.debug.FpsMeter;
import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.text.BitmapFont;
import ru.ancevt.d2d2.display.text.BitmapText;
import ru.ancevt.d2d2.lwjgl.LWJGLStarter;

public class D2D2Demo_BitmapFonts {

    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, D2D2Demo_BitmapFonts.class.getName()));
        Root root = D2D2.getStage().getRoot();

        BitmapFont font2 = BitmapFont.loadBitmapFont("PressStart2P.bmf");
        BitmapText bitmapText2 = new BitmapText(font2);
        bitmapText2.setText("PRESSSTART.bmf`` алалала");

        BitmapFont.setDefaultBitmapFont(font2);

        root.add(bitmapText2, 0, 100);

        //bitmapText2.setScale(2,2);

        root.add(new FpsMeter());

        D2D2.loop();
    }
}
