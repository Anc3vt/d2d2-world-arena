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
package com.ancevt.d2d2.starter.lwjgl;

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
