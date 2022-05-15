
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
