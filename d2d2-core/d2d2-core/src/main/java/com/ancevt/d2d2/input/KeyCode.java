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
package com.ancevt.d2d2.input;

public class KeyCode {

    public static final int UP = 265;
    public static final int LEFT = 263;
    public static final int DOWN = 264;
    public static final int RIGHT = 262;
    public static final int LEFT_SHIFT = 340;
    public static final int RIGHT_SHIFT = 344;
    public static final int ENTER = 257;
    public static final int DELETE = 261;
    public static final int BACKSPACE = 259;
    public static final int LEFT_CONTROL = 341;
    public static final int RIGHT_CONTROL = 345;
    public static final int LEFT_ALT = 342;
    public static final int RIGHT_ALT = 346;
    public static final int PAGE_UP = 266;
    public static final int PAGE_DOWN = 267;
    public static final int ESCAPE = 256;
    public static final int TAB = 258;
    public static final int HOME = 268;
    public static final int END = 269;
    public static final int SPACE = 32;

    public static final int F1 = 290;
    public static final int F2 = 291;
    public static final int F3 = 292;
    public static final int F4 = 293;
    public static final int F5 = 294;
    public static final int F6 = 295;
    public static final int F7 = 296;
    public static final int F8 = 297;
    public static final int F9 = 298;
    public static final int F10 = 299;
    public static final int F11 = 300;
    public static final int F12 = 301;

    public static final int A = 65;
    public static final int B = 66;
    public static final int C = 67;
    public static final int D = 68;
    public static final int E = 69;
    public static final int F = 70;
    public static final int G = 71;
    public static final int H = 72;
    public static final int I = 73;
    public static final int J = 74;
    public static final int K = 75;
    public static final int L = 76;
    public static final int M = 77;
    public static final int N = 78;
    public static final int O = 79;
    public static final int P = 80;
    public static final int Q = 81;
    public static final int R = 82;
    public static final int S = 83;
    public static final int T = 84;
    public static final int U = 85;
    public static final int V = 86;
    public static final int W = 87;
    public static final int X = 88;
    public static final int Y = 89;
    public static final int Z = 90;


    public static boolean isShift(int keyCode) {
        return keyCode == LEFT_SHIFT || keyCode == RIGHT_SHIFT;
    }

    public static boolean isControl(int keyCode) {
        return keyCode == LEFT_CONTROL || keyCode == RIGHT_CONTROL;
    }

    public static boolean isAlt(int keyCode) {
        return keyCode == LEFT_ALT || keyCode == RIGHT_ALT;
    }
}
