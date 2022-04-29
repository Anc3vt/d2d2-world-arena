/*
 *   D2D2 World
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
package com.ancevt.d2d2world.control;

import static com.ancevt.d2d2.input.KeyCode.DOWN;
import static com.ancevt.d2d2.input.KeyCode.ENTER;
import static com.ancevt.d2d2.input.KeyCode.ESCAPE;
import static com.ancevt.d2d2.input.KeyCode.LEFT;
import static com.ancevt.d2d2.input.KeyCode.LEFT_CONTROL;
import static com.ancevt.d2d2.input.KeyCode.RIGHT;
import static com.ancevt.d2d2.input.KeyCode.RIGHT_CONTROL;
import static com.ancevt.d2d2.input.KeyCode.SPACE;
import static com.ancevt.d2d2.input.KeyCode.UP;

public class LocalPlayerController extends Controller {

    public LocalPlayerController() {

    }

    public void key(int keyCode, char keyChar, boolean down) {
        switch (keyChar) {
            case 'A' -> setLeft(down);
            case 'W' -> setUp(down);
            case 'D' -> setRight(down);
            case 'S' -> setDown(down);
            case 'M' -> setA(down);
            case 'N' -> setB(down);
            case 'E' -> setC(down);
        }

        switch (keyCode) {
            case LEFT -> setLeft(down);
            case RIGHT -> setRight(down);
            case UP -> setUp(down);
            case DOWN -> setDown(down);
            case SPACE -> setA(down);
            case LEFT_CONTROL, RIGHT_CONTROL -> setB(down);
            case ENTER -> setC(down);
            case ESCAPE -> setBack(down);
        }
    }
}