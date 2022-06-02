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

package com.ancevt.d2d2world.editor;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.mapkit.AreaMapkit;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class Cursor extends Sprite {

    private MapkitItem mapkitItem;

    public Cursor() {
        setAlpha(0.5f);
    }

    public void setMapKitItem(MapkitItem mapkitItem) {
        this.mapkitItem = mapkitItem;

        if (mapkitItem != null) {
            setTexture(mapkitItem.getIcon().getTexture());

            if (mapkitItem.getMapkit() instanceof AreaMapkit) {
                setScale(10f, 10f);
                setColor(mapkitItem.getIcon().getColor());
            } else {
                setScale(1f, 1f);
                setColor(Color.WHITE);
            }

            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    public MapkitItem getMapkitItem() {
        return mapkitItem;
    }
}
