/*
 *   D2D2 core
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
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
package com.ancevt.d2d2.display.texture;

import com.ancevt.util.args.Args;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class TextureAtlas {

    private final int id;

    private int width;
    private int height;

    private boolean disposed;

    private TextureAtlas(int id) {
        this.id = id;
    }

    public TextureAtlas(int id, int width, int height) {
        this(id);
        setUp(width, height);
    }

    final void setUp(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Texture createTexture() {
        return createTexture(0, 0, getWidth(), getHeight());
    }

    public Texture createTexture(int x, int y, int width, int height) {
        return new Texture(this, x, y, width, height);
    }

    /**
     * 16,16,48,48
     *
     * @param textureCoords
     * @return
     */
    public Texture createTexture(String textureCoords) {
        var a = Args.of(textureCoords, ',');
        return new Texture(this, a.next(int.class), a.next(int.class), a.next(int.class), a.next(int.class));
    }

    public static @NotNull String convertCoords(@NotNull String textureCoords) {
        if (textureCoords.contains("h")) {
            StringTokenizer stringTokenizer = new StringTokenizer(textureCoords, "h");
            String firstToken = stringTokenizer.nextToken().trim();
            int count = parseInt(stringTokenizer.nextToken().trim());
            int[] textureCoordsInts = get4Values(firstToken);
            StringBuilder stringBuilder = new StringBuilder();
            int x = textureCoordsInts[0];
            int y = textureCoordsInts[1];
            int width = textureCoordsInts[2];
            int height = textureCoordsInts[3];

            int currentX = 0;
            for (int i = 0; i < count; i++) {
                stringBuilder.append(x + currentX).append(',');
                stringBuilder.append(y).append(',');
                stringBuilder.append(width).append(',');
                stringBuilder.append(height);

                if (i != count - 1) {
                    stringBuilder.append(';');
                }

                currentX += width;
            }

            textureCoords = stringBuilder.toString();
        } else if (textureCoords.contains("v")) {
            StringTokenizer stringTokenizer = new StringTokenizer(textureCoords, "v");
            String firstToken = stringTokenizer.nextToken().trim();
            int count = parseInt(stringTokenizer.nextToken().trim());
            int[] textureCoordsInts = get4Values(firstToken);
            StringBuilder stringBuilder = new StringBuilder();
            int x = textureCoordsInts[0];
            int y = textureCoordsInts[1];
            int width = textureCoordsInts[2];
            int height = textureCoordsInts[3];

            int currentY = 0;
            for (int i = 0; i < count; i++) {
                stringBuilder.append(x).append(',');
                stringBuilder.append(y + currentY).append(',');
                stringBuilder.append(width).append(',');
                stringBuilder.append(height);

                if (i != count - 1) {
                    stringBuilder.append(';');
                }

                currentY += height;
            }

            textureCoords = stringBuilder.toString();
        }

        return textureCoords;
    }

    /**
     * 16,16,48,48; 32,16,48,48; 48,16,48,48
     * or
     * 16,16,48,48h3
     */
    public Texture[] createTextures(@NotNull String textureCoords) {
        textureCoords = convertCoords(textureCoords);
        if (textureCoords.endsWith(";")) {
            textureCoords = textureCoords.substring(0, textureCoords.length() - 2);
        }
        StringTokenizer stringTokenizer = new StringTokenizer(textureCoords, ";");
        Texture[] textures = new Texture[stringTokenizer.countTokens()];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = createTexture(stringTokenizer.nextToken().trim());
        }

        return textures;
    }

    @Contract("_ -> new")
    private static int @NotNull [] get4Values(@NotNull String coords) {
        String[] split = coords.split(",");
        return new int[]{
                parseInt(split[0]),
                parseInt(split[1]),
                parseInt(split[2]),
                parseInt(split[3]),
        };
    }

    public int getId() {
        return id;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    void setDisposed(boolean disposed) {
        this.disposed = disposed;
    }

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public String toString() {
        return "TextureAtlas{" +
                "id=" + id +
                ", width=" + width +
                ", height=" + height +
                ", disposed=" + disposed +
                '}';
    }

    public static void main(String[] args) {
        TextureAtlas textureAtlas = new TextureAtlas(0, 1000, 1000);

        Texture[] textures = textureAtlas.createTextures("0,0,16,16; 16,0,16,16; 0,0,16,16; 32,0,16,16");
        System.out.println(Arrays.toString(textures));
    }
}






























