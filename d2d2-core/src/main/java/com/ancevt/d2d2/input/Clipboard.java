
package com.ancevt.d2d2.input;

import com.ancevt.d2d2.D2D2;

public class Clipboard {
    public static void set(String string) {
        D2D2.getBackend().putToClipboard(string);
    }

    public static String get() {
        return D2D2.getBackend().getStringFromClipboard();
    }
}
