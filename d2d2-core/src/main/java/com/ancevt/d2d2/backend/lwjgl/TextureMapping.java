
package com.ancevt.d2d2.backend.lwjgl;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TextureMapping {
    private final Map<Integer, Integer> ids;
    private final Map<Integer, BufferedImage> images;

    public TextureMapping() {
        ids = new HashMap<>();
        images = new HashMap<>();
    }

    public Map<Integer, Integer> ids() {
        return ids;
    }

    public Map<Integer, BufferedImage> images() {
        return images;
    }
}
