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

package com.ancevt.d2d2world;

import com.ancevt.d2d2.display.texture.Texture;

import static com.ancevt.d2d2.D2D2.getTextureManager;

public class D2D2WorldAssets {

    public static void load() {
        getTextureManager().loadTextureDataInfo("d2d2-world.inf");
    }

    public static Texture getPickupBubbleTexture32() {
        return getTextureManager().getTexture("d2d2-world-pickup-bubble-32px");
    }

    public static Texture getPickupBubbleTexture16() {
        return getTextureManager().getTexture("d2d2-world-pickup-bubble-16px");
    }

    public static Texture getRopeTexture() {
        return getTextureManager().getTexture("d2d2-world-rope");
    }

    public static Texture getWaterBubbleTexture() {
        return getTextureManager().getTexture("d2d2-world-water-bubble");
    }
}
