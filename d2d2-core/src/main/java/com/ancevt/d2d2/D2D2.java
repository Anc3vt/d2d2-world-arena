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
package com.ancevt.d2d2;

import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2.display.texture.TextureManager;
import com.ancevt.d2d2.starter.D2D2Starter;

public class D2D2 {

    private static final TextureManager textureManager = new TextureManager();
    private static D2D2Starter starter;

    private D2D2() {
    }

    public static void setFullscreen(boolean value) {
        starter.setFullscreen(value);
    }

    public static boolean isFullscreen() {
        return starter.isFullscreen();
    }

    public static void setSmoothMode(boolean value) {
        starter.setSmoothMode(value);
    }

    public static boolean isSmoothMode() {
        return starter.isSmoothMode();
    }

    public static D2D2Starter getStarter() {
        return starter;
    }

    public static Root init(D2D2Starter starter) {
        Root root;
        D2D2.starter = starter;
        starter.create();
        getStage().setRoot(root = new Root());
        return root;
    }

    public static Stage getStage() {
        return starter.getStage();
    }

    public static void loop() {
        starter.start();
    }

    public static TextureManager getTextureManager() {
        return textureManager;
    }

    public static void exit() {
        starter.stop();
    }

}
