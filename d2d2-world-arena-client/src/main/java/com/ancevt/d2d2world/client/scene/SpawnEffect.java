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

import com.ancevt.d2d2.display.*;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;
import org.jetbrains.annotations.NotNull;

public class SpawnEffect extends Container {

    private float speed1 = +8.0f;
    private float speed2 = -4.0f;

    private final Sprite sprite1;
    private final Sprite sprite2;

    private final Texture texture;

    private final int textureX;
    private final int textureY;

    private float y1;
    private float y2;

    private int time;

    public SpawnEffect() {
        texture = D2D2WorldArenaClientAssets.getSpawnEffectTexture();

        textureX = texture.x();
        textureY = texture.y();

        sprite1 = new Sprite();
        sprite2 = new Sprite();

        add(sprite1, -texture.width() / 2f, -texture.height() / 2f);
        add(sprite2, -texture.width() / 2f, -texture.height() / 2f);

        setAlpha(0.0f);

        setScaleY(1.5f);
    }

    public void setColor1(Color color) {
        sprite1.setColor(color);
    }

    public Color getColor1() {
        return sprite1.getColor();
    }

    public void setColor2(Color color) {
        sprite2.setColor(color);
    }

    public Color getColor2() {
        return sprite2.getColor();
    }

    @Override
    public void onEachFrame() {
        time++;

        y1 += speed1;
        y2 += speed2;

        sprite1.setTexture(texture.getSubtexture(0, (int) y1, texture.width(), 32));
        sprite2.setTexture(texture.getSubtexture(0, (int) y2, texture.width(), 32));

        if (y1 < 0) {
            y1 = texture.height() - 32;
        } else if (y1 > texture.height() - 32) {
            y1 = 0;
        }

        if (y2 < 0) {
            y2 = texture.height() - 32;
        } else if (y2 > texture.height() - 32) {
            y2 = 0;
        }

        if (time < 10 && getAlpha() < 1f) {
            setAlpha(getAlpha() + 0.05f);
        }

        if (time > 40 && getAlpha() > 0) {
            setAlpha(getAlpha() - 0.05f);
        }

        if (getAlpha() <= 0) removeFromParent();
    }

    public static void doSpawnEffect(float x, float y, @NotNull IContainer targetParent) {
        targetParent.add(new SpawnEffect(), x, y + 48);
    }

}
