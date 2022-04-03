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
package com.ancevt.d2d2.demo;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.starter.lwjgl.LWJGLStarter;

public class D2D2Demo_AddToStageContainer {
    public static void main(String[] args) {
        D2D2.init(new LWJGLStarter(800, 600, "(floating)"));
        Root root = D2D2.getStage().getRoot();

        DisplayObjectContainer container = new DisplayObjectContainer();

        Sprite sprite = new Sprite("satellite") {
            {
                addEventListener(Event.ADD_TO_STAGE, e -> {
                    System.out.println(e);
                });
            }
        };



        // sprite.dispatchEvent(EventPool.simpleEventSingleton(Event.ADD_TO_STAGE, sprite));

        root.add(container);

        container.add(sprite);
        D2D2.loop();
    }
}
