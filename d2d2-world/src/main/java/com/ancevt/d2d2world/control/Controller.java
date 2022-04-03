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

import java.util.function.Consumer;

public class Controller {

    private boolean up, down, left, right, a, b, c, back;
    private boolean enabled;

    private Consumer<Controller> controllerChangeListener;

    public void setControllerChangeListener(Consumer<Controller> controllerChangeListener) {
        this.controllerChangeListener = controllerChangeListener;
    }

    public Consumer<Controller> getControllerChangeListener() {
        return controllerChangeListener;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        back = up = down = left = right = a = b = c = false;
        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setBack(boolean back) {
        this.back = back;
        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public boolean isBack() {
        return back;
    }

    public boolean isC() {
        return c;
    }

    public void setC(boolean c) {
        if (!enabled) return;
        this.c = c;
        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        if (!enabled) return;
        this.b = b;
        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public boolean isA() {
        return a;
    }

    public void setA(boolean a) {
        if (!enabled) return;
        this.a = a;
        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        if (!enabled) return;
        this.right = right;
        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        if (!enabled) return;
        this.left = left;
        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        if (!enabled) return;
        this.down = down;
        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        if (!enabled) return;
        this.up = up;
        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public void reset() {
        setC(false);
        setB(false);
        setA(false);
        setLeft(false);
        setRight(false);
        setDown(false);
        setUp(false);
    }


    @Override
    public String toString() {
        return "Controller[" +
                String.format(
                        "%c%c %c%c%c",
                        isLeft() ? 'L' : 'l',
                        isRight() ? 'R' : 'r',
                        isC() ? 'C' : 'c',
                        isB() ? 'B' : 'b',
                        isA() ? 'A' : 'a'
                ) +
                "]";
    }

    public void applyState(int s) {
        if (!enabled) return;

        final boolean a = (s & 1) != 0;
        final boolean b = (s & 2) != 0;
        final boolean c = (s & 4) != 0;
        final boolean l = (s & 8) != 0;
        final boolean r = (s & 16) != 0;

        if (a != isA()) this.a = a;
        if (b != isB()) this.b = b;
        if (c != isC()) this.c = c;
        if (l != isLeft()) this.left = l;
        if (r != isRight()) this.right = r;

        if (controllerChangeListener != null) controllerChangeListener.accept(this);
    }

    public int getState() {
        int r = 0;
        if (isA()) r += 1;
        if (isB()) r += 2;
        if (isC()) r += 4;
        if (isLeft()) r += 8;
        if (isRight()) r += 16;
        return r;
    }
}
