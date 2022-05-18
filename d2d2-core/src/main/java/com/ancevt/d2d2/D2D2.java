
package com.ancevt.d2d2;

import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.texture.TextureManager;
import com.ancevt.d2d2.backend.D2D2Backend;
import org.jetbrains.annotations.NotNull;

public class D2D2 {

    private static final TextureManager textureManager = new TextureManager();
    private static D2D2Backend backend;

    private D2D2() {
    }

    public static void setFullscreen(boolean value) {
        backend.setFullscreen(value);
    }

    public static boolean isFullscreen() {
        return backend.isFullscreen();
    }

    public static void setSmoothMode(boolean value) {
        backend.setSmoothMode(value);
    }

    public static boolean isSmoothMode() {
        return backend.isSmoothMode();
    }

    public static D2D2Backend getBackend() {
        return backend;
    }

    public static @NotNull Root init(@NotNull D2D2Backend backend) {
        D2D2.backend = backend;
        backend.create();
        Root root = new Root();
        getStage().setRoot(root);
        return root;
    }

    public static Stage getStage() {
        return backend.getStage();
    }

    public static void loop() {
        backend.start();
    }

    public static TextureManager getTextureManager() {
        return textureManager;
    }

    public static void exit() {
        backend.stop();
    }

}
