
package com.ancevt.d2d2.display;

public interface IRenderer {

    void init(long windowId);
    void reshape(int width, int height);
    void renderFrame();

}
