
package com.ancevt.d2d2.backend.lwjgl;

import com.ancevt.d2d2.display.texture.TextureAtlas;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class TextureLoadQueue {

    private Queue<LoadTask> tasks;

    public TextureLoadQueue() {
        tasks = new LinkedList<>();
    }

    public void putLoad(LoadTask loadTask) {
        tasks.add(loadTask);
    }

    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    public LoadTask poll() {
        return tasks.poll();
    }

    public static class LoadTask {

        private final TextureAtlas textureAtlas;
        private final int width;
        private final int height;
        private final ByteBuffer byteBuffer;

        public LoadTask(TextureAtlas textureAtlas, int width, int height, ByteBuffer byteBuffer) {

            this.textureAtlas = textureAtlas;
            this.width = width;
            this.height = height;
            this.byteBuffer = byteBuffer;
        }

        public TextureAtlas getTextureAtlas() {
            return textureAtlas;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public ByteBuffer getByteBuffer() {
            return byteBuffer;
        }
    }
}
