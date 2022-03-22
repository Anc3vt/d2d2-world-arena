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
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.EventPool;
import org.jetbrains.annotations.NotNull;

public class FramedSprite extends Sprite implements IFramedDisplayObject {

    private Texture[] frameTextures;

    private boolean playing;
    private boolean loop;
    private int slowing;
    private int slowingCounter;
    private int currentFrameIndex;
    private boolean backward;

    public FramedSprite() {
        frameTextures = new Texture[0];
        slowingCounter = 0;
        setLoop(false);
        stop();
        setSlowing(DEFAULT_SLOWING);
        setName("_" + getClass().getSimpleName() + displayObjectId());
    }

    public FramedSprite(ISprite[] frameSprites, boolean cloneEach) {
        this();
        setFrameSprites(frameSprites, cloneEach);
    }

    public FramedSprite(ISprite[] frameSprites) {
        this(frameSprites, false);
    }

    public FramedSprite(Texture[] textures) {
        this();
        setFrameTextures(textures);
    }

    @Override
    public void setBackward(boolean backward) {
        this.backward = backward;
    }

    @Override
    public boolean isBackward() {
        return backward;
    }

    @Override
    public void processFrame() {
        if (!playing) return;

        slowingCounter++;
        if (slowingCounter >= slowing) {
            slowingCounter = 0;
            if(backward) prevFrame(); else nextFrame();
        }
    }

    @Override
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    @Override
    public boolean isLoop() {
        return loop;
    }

    @Override
    public void setSlowing(int slowing) {
        this.slowing = slowing;
    }

    @Override
    public int getSlowing() {
        return slowing;
    }

    @Override
    public void nextFrame() {
        setFrame(++currentFrameIndex);
        drawCurrentFrame();
    }

    @Override
    public void prevFrame() {
        currentFrameIndex--;
        if (currentFrameIndex < 0) {
            if(loop) {
                currentFrameIndex = getFrameCount() - 1;
            } else {
                currentFrameIndex = 0;
            }
        }
        drawCurrentFrame();
    }

    @Override
    public void setFrame(int frameIndex) {
        this.currentFrameIndex = frameIndex;
        slowingCounter = 0;

        if (this.currentFrameIndex >= frameTextures.length) {
            if (loop) {
                this.currentFrameIndex = 0;
                dispatchEvent(EventPool.createEvent(Event.COMPLETE, this));
                play();
            } else {
                this.currentFrameIndex--;
                stop();
                dispatchEvent(EventPool.createEvent(Event.COMPLETE, this));
            }
        }

        drawCurrentFrame();
    }

    @Override
    public int getFrame() {
        return currentFrameIndex;
    }

    @Override
    public int getFrameCount() {
        return frameTextures.length;
    }

    @Override
    public void play() {
        playing = true;
    }

    @Override
    public void stop() {
        playing = false;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void setFrameTextures(Texture[] textures) {
        this.frameTextures = textures;
        if (textures.length > 0) {
            setFrame(0);
        }
    }

    @Override
    public Texture[] getFrameTextures() {
        return this.frameTextures;
    }

    @Override
    public void setFrameSprites(ISprite @NotNull [] sprites, boolean cloneEach) {
        Texture[] textures = new Texture[sprites.length];
        for (int i = 0; i < sprites.length; i++) {
            textures[i] = sprites[i].getTexture();
        }
        setFrameTextures(textures);
    }

    @Override
    public void setFrameSprites(ISprite[] sprites) {
        setFrameSprites(sprites, false);
    }

    @Override
    public ISprite[] getFrameSprites() {
        ISprite[] sprites = new Sprite[frameTextures.length];
        for (int i = 0; i < frameTextures.length; i++) {
            sprites[i] = new Sprite(frameTextures[i]);
        }
        return sprites;
    }

    private void drawCurrentFrame() {
        super.setTexture(frameTextures[currentFrameIndex]);
    }

    @Override
    public void setTexture(Texture value) {
        throw new IllegalStateException("Unable to set texture directly. Use setFrameTextures([]) instead");
    }
}






