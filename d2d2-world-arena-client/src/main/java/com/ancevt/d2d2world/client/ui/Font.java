
package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2.display.text.BitmapFont;

public class Font {

    private static final String BMF_FILE_NAME = "Terminus_Bold_8x16_spaced_shadowed_v1.bmf";
    private static BitmapFont bitmapFont;

    public static BitmapFont getBitmapFont() {
        if (bitmapFont == null) {
            bitmapFont = BitmapFont.loadBitmapFont(BMF_FILE_NAME);
        }
        return bitmapFont;
    }
}
