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
package ru.ancevt.d2d2;

import ru.ancevt.d2d2.display.Root;
import ru.ancevt.d2d2.display.Stage;
import ru.ancevt.d2d2.display.texture.TextureManager;

public class D2D2 {

    private static D2D2Starter starter;
    private static final TextureManager textureManager = new TextureManager();

    private D2D2() {
    }

    public static D2D2Starter getStarter() {
        return starter;
    }

    public static void init(D2D2Starter starter) {
        D2D2.starter = starter;
        starter.create();
        getStage().setRoot(new Root());
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
