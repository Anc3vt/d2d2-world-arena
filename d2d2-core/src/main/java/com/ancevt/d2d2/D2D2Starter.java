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
package com.ancevt.d2d2;

import com.ancevt.d2d2.display.Stage;

public interface D2D2Starter {

    void create();

    void start();

    void setSize(int width, int height);

    int getWidth();

    int getHeight();

    void setTitle(String title);

    String getTitle();

    Stage getStage();

    void setVisible(boolean visible);

    boolean isVisible();

    void stop();

    void setMouseVisible(boolean mouseVisible);

    boolean isMouseVisible();

    void putToClipboard(String string);

    String getStringFromClipboard();
}
