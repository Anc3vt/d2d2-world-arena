package ru.ancevt.d2d2.display;

import ru.ancevt.d2d2.display.texture.ITextureEngine;

public interface IRenderer {

    void init(long windowId);
    void reshape(int width, int height);
    void renderFrame();

}
