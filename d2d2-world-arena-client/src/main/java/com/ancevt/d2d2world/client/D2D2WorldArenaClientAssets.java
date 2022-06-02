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

package com.ancevt.d2d2world.client;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.texture.Texture;
import com.ancevt.d2d2world.gameobject.IGameObject;
import org.jetbrains.annotations.NotNull;

import static com.ancevt.d2d2.D2D2.getTextureManager;

public class D2D2WorldArenaClientAssets {

    private static Aim aim;

    public static void load() {
        getTextureManager().loadTextureDataInfo("d2d2-world-arena-desktop.inf");
    }

    public static Texture getPlayerArrowTexture() {
        return getTextureManager().getTexture("d2d2-world-arena-desktop-player-arrow");
    }

    public static Texture getControlsHelpTexture() {
        return getTextureManager().getTexture("d2d2-world-arena-desktop-controls-help");
    }

    public static Texture getAimTexture() {
        return getTextureManager().getTexture("d2d2-world-arena-desktop-aim");
    }

    public static Texture getSpawnEffectTexture() {
        return getTextureManager().getTexture("d2d2-world-arena-desktop-spawn-effect");
    }

    public static Texture getChatBubbleTexture() {
        return getTextureManager().getTexture("d2d2-world-arena-desktop-chat-bubble");
    }

    public static Texture getPreloaderTexture() {
        return getTextureManager().getTexture("d2d2-world-arena-desktop-preloader");
    }

    public static Texture getWarningTexture() {
        return getTextureManager().getTexture("d2d2-world-arena-desktop-warning");
    }

    public static Texture getCharSelectDoorTexture() {
        return getTextureManager().getTexture("d2d2-world-arena-desktop-char-select-door");
    }

    public static Aim getAim() {
        return aim == null ? aim = new Aim() : aim;
    }

    public static class Aim extends Container {
        private final Sprite sprite;

        public Aim() {
            sprite = new Sprite(getAimTexture());
            add(sprite, -sprite.getWidth()/2, -sprite.getHeight()/2 - 3);
        }

        @Override
        public void onEachFrame() {
            if(getScaleX() > 1.0) {
                toScale(0.9f, 0.9f);
                if(getScaleX() < 0.1) {
                    setScale(1f, 1f);
                }
            }
        }

        public void setColor(Color color) {
            sprite.setColor(color);
        }

        public Color getColor() {
            return sprite.getColor();
        }

        public void attack() {
            setScale(2.0f, 2.0f);
        }

        public void setTarget(@NotNull IGameObject gameObject) {
            setXY(gameObject.getX(), gameObject.getY());
        }
    }

}
