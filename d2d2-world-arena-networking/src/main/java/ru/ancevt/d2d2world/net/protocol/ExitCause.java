/*
 *   D2D2 World Arena Networking
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
package ru.ancevt.d2d2world.net.protocol;

import java.util.Arrays;

public enum ExitCause {

    NORMAL_EXIT(1),
    LOST_CONNECTION(2);

    private final int value;

    ExitCause(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ExitCause ofValue(int value) {
        return Arrays.stream(ExitCause.values()).filter(e -> e.value == value).findAny().orElseThrow();
    }
}