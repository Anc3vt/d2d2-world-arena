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

package com.ancevt.d2d2world.world;

import com.ancevt.d2d2.display.Color;
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
                if (o instanceof Scenery scenery && scenery.isStatic()) {
                    Texture textureRegion = scenery.getTexture();
                    int x = (int) scenery.getX();
                    int y = (int) scenery.getY();
                    float scaleX = scenery.getScaleX();
                    float scaleY = scenery.getScaleY();
                    float alpha = scenery.getAlpha();
                    float rotation = scenery.getRotation();
                    int repeatX = scenery.getRepeatX();
                    int repeatY = scenery.getRepeatY();
                    Color color = scenery.getColor();
                    comb.append(textureRegion, x, y, color, scaleX, scaleY, alpha, rotation, repeatX, repeatY);
                }
            }
        }
        return new PackedScenery(comb.createTextureAtlas());
    }
}
