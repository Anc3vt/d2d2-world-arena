/*
 *   D2D2 World Arena Desktop
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
package ru.ancevt.d2d2world.game.scene;

import ru.ancevt.d2d2.display.DisplayObjectContainer;
import ru.ancevt.d2d2.display.Sprite;
import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2world.game.ui.Font;
import ru.ancevt.d2d2world.game.ui.UiText;

public class ThanksTo extends DisplayObjectContainer {

    public ThanksTo(Texture texture, String name) {
        Sprite sprite = new Sprite(texture);

        add(sprite);
        UiText uiText = new UiText();
        uiText.setText(name);

        int textWidth = name.length() * Font.getBitmapFont().getCharInfo('0').width();

        uiText.setXY((sprite.getWidth() - textWidth) / 2, sprite.getHeight() + 10);
        add(uiText);
    }
}
