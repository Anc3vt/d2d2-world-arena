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
package ru.ancevt.d2d2world.world;

import ru.ancevt.d2d2.display.texture.Texture;
import ru.ancevt.d2d2.display.texture.TextureCombiner;
import ru.ancevt.d2d2world.gameobject.IGameObject;
import ru.ancevt.d2d2world.gameobject.Scenery;
import ru.ancevt.d2d2world.map.Room;

public class SceneryPacker {
    public static PackedScenery pack(Room room, int layerFrom, int layerTo) {

        TextureCombiner comb = new TextureCombiner(room.getWidth(), room.getHeight());

        for (int layer = layerFrom; layer <= layerTo; layer++) {
            for (int i = 0; i < room.getGameObjectsCount(layer); i++) {
                IGameObject o = room.getGameObject(layer, i);
                if (o instanceof Scenery s) {
                    Texture textureRegion = s.getTexture();
                    int x = (int) s.getX();
                    int y = (int) s.getY();
                    float scaleX = s.getScaleX();
                    float scaleY = s.getScaleY();
                    float alpha = s.getAlpha();
                    float rotation = s.getRotation();
                    int repeatX = s.getRepeatX();
                    int repeatY = s.getRepeatY();

                    comb.append(textureRegion, x, y, scaleX, scaleY, alpha, rotation, repeatX, repeatY);
                }
            }
        }

        return new PackedScenery(comb.createTextureAtlas());
    }
}
