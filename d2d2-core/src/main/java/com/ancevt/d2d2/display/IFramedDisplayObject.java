
package com.ancevt.d2d2.display;

import com.ancevt.d2d2.display.texture.Texture;

public interface IFramedDisplayObject extends IDisplayObject {
    int DEFAULT_SLOWING = 5;

    void processFrame();

    void setLoop(boolean loop);

    boolean isLoop();

    void setSlowing(int slowing);

    int getSlowing();

    void nextFrame();

    void prevFrame();

    void setFrame(int frameIndex);

    int getFrame();

    int getFrameCount();

    void setBackward(boolean value);

    boolean isBackward();

    void play();

    void stop();

    boolean isPlaying();

    void setFrameTextures(Texture[] textures);

    Texture[] getFrameTextures();

    void setFrameSprites(ISprite[] sprites, boolean cloneEach);

    void setFrameSprites(ISprite[] sprites);

    ISprite[] getFrameSprites();
}
