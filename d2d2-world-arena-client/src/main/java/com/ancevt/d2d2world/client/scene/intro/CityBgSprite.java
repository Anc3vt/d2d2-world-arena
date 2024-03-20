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
package com.ancevt.d2d2world.client.scene.intro;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;

public class CityBgSprite extends Sprite {

    private static final float SPEED = 1f;
    private static Texture texture;

    public static Texture createOrGetTexture() {
        if (texture == null) {
            texture = D2D2.getTextureManager().loadTextureAtlas("city-bg.png").createTexture();
        }
        return texture;
    }

    public CityBgSprite() {
        super(createOrGetTexture());
        setColor(Color.BLACK);
        setRepeatX(10);
        setScale(2f,2f);
    }

    @Override
    public void onEachFrame() {
        moveX(-SPEED);
        if(getX() < -getTexture().width() * 2 * getScaleX()) {
            setX(-getTexture().width() * 2);
        }
    }
}
