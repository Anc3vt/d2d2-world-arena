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
package com.ancevt.d2d2world.gameobject.area;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.data.Property;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class AreaDoorTeleport extends Area {

    public static final Color FILL_COLOR = Color.BLUE;
    private static final Color STROKE_COLOR = Color.WHITE;

    private String targetAreaName;

    public AreaDoorTeleport(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setFillColor(FILL_COLOR);
        setBorderColor(STROKE_COLOR);
    }

    @Property
    public void setTargetAreaName(String targetAreaName) {
        this.targetAreaName = targetAreaName;
    }

    @Property
    public String getTargetAreaName() {
        return targetAreaName;
    }
}




















