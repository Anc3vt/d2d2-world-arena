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

public class FramedDisplayObjectContainer extends DisplayObjectContainer implements IFramedDisplayObject {

    private ISprite[] frames;

    private boolean playing;
    private boolean loop;
    private int slowing;
    private int slowingCounter;
    private int currentFrameIndex;
    private ISprite currentSprite;
    private boolean backward;

    public FramedDisplayObjectContainer(ISprite[] frameSprites, boolean cloneEach) {
        this();
        setFrameSprites(frameSprites, cloneEach);
    }

    public FramedDisplayObjectContainer(ISprite[] frameSprites) {
        this(frameSprites, false);
    }

    public FramedDisplayObjectContainer(Texture[] textures) {
        this();
        setFrameTextures(textures);
    }

    public FramedDisplayObjectContainer() {
        frames = new ISprite[0];
        slowingCounter = 0;
        setLoop(false);
        stop();
        setSlowing(DEFAULT_SLOWING);
        setName("_" + getClass().getSimpleName() + displayObjectId());
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
        if (!playing)
            return;

        slowingCounter++;
        if (slowingCounter >= slowing) {
            slowingCounter = 0;
            if (backward) prevFrame();
            else nextFrame();
        }

    }

    @Override
    public void setLoop(boolean b) {
        this.loop = b;
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
            if (loop) {
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

        if (this.currentFrameIndex >= frames.length) {
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

    private void drawCurrentFrame() {
        if (currentSprite != null && currentSprite.getParent() != null) {
            currentSprite.removeFromParent();
        }

        currentSprite = frames[currentFrameIndex];
        if (currentSprite != null) {
            this.add(currentSprite);
        }
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void setFrameTextures(Texture[] textures) {
        frames = new ISprite[textures.length];
        for (int i = 0; i < textures.length; i++) {
            frames[i] = new Sprite(textures[i]);
        }
        if (frames.length > 0) {
            setFrame(0);
        }
    }

    @Override
    public Texture[] getFrameTextures() {
        Texture[] textures = new Texture[frames.length];
        for (int i = 0; i < frames.length; i++) {
            textures[i] = frames[i].getTexture();
        }
        return textures;
    }

    @Override
    public void setFrameSprites(ISprite[] sprites, boolean cloneEach) {
        if (!cloneEach) {
            frames = sprites;
        } else {
            frames = new ISprite[sprites.length];
            for (int i = 0; i < sprites.length; i++) {
                ISprite frame = sprites[i].cloneSprite();
                frames[i] = frame;
            }
        }
        if (sprites.length > 0) {
            setFrame(0);
        }
    }

    @Override
    public void setFrameSprites(ISprite[] sprites) {
        setFrameSprites(sprites, false);
    }

    @Override
    public ISprite[] getFrameSprites() {
        return frames;
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
    public int getFrame() {
        return currentFrameIndex;
    }

    @Override
    public int getFrameCount() {
        return frames.length;
    }

    public final String getFrameListString() {
        final StringBuilder sb = new StringBuilder();

        for (ISprite frame : frames) {
            sb.append(frame).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "FramedDisplayObjectContainer{" +
                "playing=" + playing +
                ", loop=" + loop +
                ", slowing=" + slowing +
                ", frameIndex=" + currentFrameIndex +
                '}';
    }
}
