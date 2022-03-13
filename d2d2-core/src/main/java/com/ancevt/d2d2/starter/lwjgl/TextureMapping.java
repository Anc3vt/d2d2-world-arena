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
