
package com.ancevt.d2d2world;

public class D2D2World {

    public static final float ORIGIN_WIDTH = 800f;
    public static final float ORIGIN_HEIGHT = 600f;

    public static final float SCALE = 1.6f;//2.5f;

    private static boolean server;
    private static boolean editor;

    private D2D2World() {
    }

    public static void init(boolean server, boolean editor) {
        D2D2World.server = server;
        D2D2World.editor = editor;
        D2D2WorldAssets.load();
    }

    public static boolean isServer() {
        return server;
    }

    public static boolean isEditor() {
        return editor;
    }



}
