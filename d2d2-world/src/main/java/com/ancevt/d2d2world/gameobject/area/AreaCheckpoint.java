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
import com.ancevt.d2d2world.constant.CheckpointType;
import com.ancevt.d2d2world.constant.Direction;
import com.ancevt.d2d2world.gameobject.IDirectioned;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class AreaCheckpoint extends Area implements IDirectioned {

    public static final Color FILL_COLOR = Color.GREEN;
    private static final Color STROKE_COLOR = Color.WHITE;

    private static final String STR_START = "Start";
    private static final String STR_FINISH = "Finish";
    private static final String STR_CONTINUE = "Continue";

    private static final int DEFAULT_CHECKPOINT_TYPE = CheckpointType.START;

    private static final int DEFAULT_DIRECTION = Direction.RIGHT;

    private int checkpointType;

    private int direction;

    public AreaCheckpoint(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setFillColor(FILL_COLOR);
        setBorderColor(STROKE_COLOR);
        setDirection(DEFAULT_DIRECTION);
        setCheckpointType(DEFAULT_CHECKPOINT_TYPE);
        updateText();
    }

    public void setCheckpointType(int checkPointType) {
        this.checkpointType = checkPointType;
        updateText();
    }

    public int getCheckpointType() {
        return checkpointType;
    }

    @Override
    public void setDirection(int direction) {
        this.direction = direction;
        updateText();
    }

    @Override
    public int getDirection() {
        return direction;
    }

    private void updateText() {
        final StringBuilder s = new StringBuilder();

        if (getDirection() == Direction.LEFT) {
            s.append("<- ");
        } else
        if (getDirection() == Direction.RIGHT) {
            s.append("-> ");
        }

        switch (getCheckpointType()) {
            case CheckpointType.START -> s.append(STR_START);
            case CheckpointType.FINISH -> s.append(STR_FINISH);
            case CheckpointType.CONTINUE -> s.append(STR_CONTINUE);
            default -> throw new IllegalStateException("no such checkpoint type: " + getCheckpointType());
        }

        setText(s.toString());
    }

}
