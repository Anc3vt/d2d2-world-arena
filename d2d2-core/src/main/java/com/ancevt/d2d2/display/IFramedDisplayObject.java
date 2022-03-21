/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
