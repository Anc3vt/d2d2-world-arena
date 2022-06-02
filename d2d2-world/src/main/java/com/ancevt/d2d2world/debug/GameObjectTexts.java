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

package com.ancevt.d2d2world.debug;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2.event.Event.REMOVE_FROM_STAGE;

public class GameObjectTexts extends Container {

    private final Map<IGameObject, BitmapText> bitmapTextMap;
    private final World world;

    private boolean enabled;

    public GameObjectTexts(World world) {
        this.world = world;
        bitmapTextMap = new HashMap<>();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if(!enabled) {
            clear();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void clear() {
        bitmapTextMap.values().forEach(DisplayObject::removeFromParent);
        bitmapTextMap.clear();
    }

    @Override
    public void onEachFrame() {
        if (!isEnabled()) return;

        world.getGameObjects().forEach(gameObject -> {
            var bitmapText = bitmapTextMap.get(gameObject);
            if (bitmapText == null) {
                bitmapText = new BitmapText();
                bitmapText.setText(String.valueOf(gameObject.getGameObjectId()));
                bitmapText.setScale(0.5f, 0.5f);
                gameObject.removeEventListener(this, REMOVE_FROM_STAGE);
                gameObject.addEventListener(this, REMOVE_FROM_STAGE, this::gameObject_removeFromStage);
                bitmapTextMap.put(gameObject, bitmapText);
                add(bitmapText);
            }
            bitmapText.setXY(gameObject.getX(), gameObject.getY());
        });
    }

    private void gameObject_removeFromStage(@NotNull Event event) {
        if (event.getSource() instanceof IGameObject gameObject) {
            gameObject.removeEventListener(this, REMOVE_FROM_STAGE);
            BitmapText bitmapText = bitmapTextMap.get(gameObject);
            if (bitmapText != null) {
                Async.runLater(5, TimeUnit.SECONDS, bitmapText::removeFromParent);
                bitmapText.setColor(Color.DARK_GRAY);
            }
            bitmapTextMap.remove(gameObject);
        }
    }

}
