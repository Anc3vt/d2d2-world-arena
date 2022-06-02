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

package com.ancevt.d2d2world.client.scene;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.Stage;
import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;

public class ControlsHelp extends Sprite {

    private static final int SPEED = 25;

    private int direction = SPEED;

    private int tact;

    public ControlsHelp() {
        super(D2D2WorldArenaClientAssets.getControlsHelpTexture());
        setColor(new Color(0xFF, 0x80, 0xFF));
    }

    @Override
    public void onEachFrame() {
        super.onEachFrame();

        int value = getColor().getB();

        getColor().setB(value + direction);

        value = getColor().getB();

        if (value > 0xFF) {
            direction = -SPEED;
        } else if (value < 0) {
            direction = SPEED;
        }

        tact++;

        if(tact > 250) {
            setAlpha(getAlpha() - 0.075f);
            if(getAlpha() <= 0.01f) {
                removeFromParent();
            }
        }
    }

    public static void main(String[] args) {
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating"));
        D2D2World.init(false, false);
        stage.setBackgroundColor(Color.DARK_GRAY);

        stage.add(new ControlsHelp(), 0, 0);
        stage.setScale(3f, 3f);

        D2D2.loop();
    }
}
