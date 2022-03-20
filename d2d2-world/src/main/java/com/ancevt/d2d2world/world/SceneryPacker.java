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
package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2.display.texture.TextureCombiner;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.gameobject.Scenery;
import com.ancevt.d2d2world.map.Room;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SceneryPacker {
    @Contract("_, _, _ -> new")
    public static @NotNull PackedScenery pack(@NotNull Room room, int layerFrom, int layerTo) {

        TextureCombiner comb = new TextureCombiner(room.getWidth(), room.getHeight());

        for (int layer = layerFrom; layer <= layerTo; layer++) {
            for (int i = 0; i < room.getGameObjectsCount(layer); i++) {
                IGameObject o = room.getGameObject(layer, i);
                if (o instanceof Scenery scenery) {
                    Texture textureRegion = scenery.getTexture();
                    int x = (int) scenery.getX();
                    int y = (int) scenery.getY();
                    float scaleX = scenery.getScaleX();
                    float scaleY = scenery.getScaleY();
                    float alpha = scenery.getAlpha();
                    float rotation = scenery.getRotation();
                    int repeatX = scenery.getRepeatX();
                    int repeatY = scenery.getRepeatY();

                    comb.append(textureRegion, x, y, scaleX, scaleY, alpha, rotation, repeatX, repeatY);
                }
            }
        }
        return new PackedScenery(comb.createTextureAtlas());
    }
}
