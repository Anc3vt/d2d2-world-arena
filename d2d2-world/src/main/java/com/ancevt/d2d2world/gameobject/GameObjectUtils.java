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
package com.ancevt.d2d2world.gameobject;

import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.world.Layer;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.data.Properties.setProperties;

public class GameObjectUtils {

    public static IGameObject copy(IGameObject of, int newGameObjectId) {
        IGameObject result = of.getMapkitItem().createGameObject(newGameObjectId);
        return (IGameObject) setProperties(result, getProperties(of, DataEntry.newInstance()));
    }

    public static int getLayerIndex(IGameObject gameObject) {
        if (!gameObject.hasParent()) throw new IllegalStateException("Game object has no parent");

        return ((Layer) gameObject.getParent()).getIndex();
    }

}
