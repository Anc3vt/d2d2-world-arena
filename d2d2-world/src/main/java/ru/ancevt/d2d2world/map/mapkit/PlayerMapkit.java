/*
 *   D2D2 World
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
package ru.ancevt.d2d2world.map.mapkit;

import ru.ancevt.d2d2.D2D2;

public class PlayerMapkit extends Mapkit{

    private static final String TILESET_ASSET_FILE = "player-tileset.png";

    public static final String ID = "player";

    PlayerMapkit() {
        super(ID);
        setTextureAtlas(D2D2.getTextureManager().loadTextureAtlas(TILESET_ASSET_FILE));
        addItems();
    }

    private void addItems() {

    }


}
