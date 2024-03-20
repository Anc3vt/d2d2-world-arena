/**
 * Copyright (C) 2022 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
