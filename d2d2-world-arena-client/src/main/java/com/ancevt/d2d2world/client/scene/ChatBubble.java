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

import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2world.client.D2D2WorldArenaClientAssets;

public class ChatBubble extends Sprite {

    private float factor = -0.05f;

    public ChatBubble() {
        super(D2D2WorldArenaClientAssets.getChatBubbleTexture());
    }

    @Override
    public void onEachFrame() {
        setAlpha(getAlpha() + factor);
        if(getAlpha() < 0.0f || getAlpha() > 1.0f) factor = -factor;
    }

}
