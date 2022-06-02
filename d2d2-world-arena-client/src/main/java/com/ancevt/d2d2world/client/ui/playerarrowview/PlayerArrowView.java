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

package com.ancevt.d2d2world.client.ui.playerarrowview;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.Container;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2world.gameobject.IGameObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerArrowView extends Container {

    private float viewportWidth;
    private float viewportHeight;
    private final BorderedRect debugBorderedRect;

    /**
     * gameObjectId -> playerArrow
     */
    private final Map<Integer, PlayerArrow> playerArrowMap;

    private IGameObject from;

    public PlayerArrowView() {
        debugBorderedRect = new BorderedRect(null, Color.YELLOW);
        //add(debugBorderedRect);

        playerArrowMap = new HashMap<>();
    }

    public void setFrom(@NotNull IGameObject from) {
        this.from = from;

        if (playerArrowMap.containsKey(from.getGameObjectId()))
            playerArrowMap.remove(from.getGameObjectId()).removeFromParent();
    }

    public IGameObject getFrom() {
        return from;
    }

    public void createPlayerArrow(@NotNull IGameObject target, Color color) {
        if (playerArrowMap.containsKey(target.getGameObjectId())) {
            playerArrowMap.get(target.getGameObjectId()).removeFromParent();
        }

        PlayerArrow playerArrow = new PlayerArrow(this);
        playerArrow.setColor(color);
        playerArrow.setTarget(target);
        add(playerArrow);
        playerArrowMap.put(target.getGameObjectId(), playerArrow);
    }

    public void removePlayerArrow(@NotNull IGameObject target) {
        IDisplayObject playerArrow = playerArrowMap.remove(target.getGameObjectId());
        if (playerArrow != null) {
            playerArrow.removeFromParent();
        }
    }

    public void clear() {
        playerArrowMap.values().forEach(IDisplayObject::removeFromParent);
        playerArrowMap.clear();
    }

    public float getViewportWidth() {
        return viewportWidth;
    }

    public float getViewportHeight() {
        return viewportHeight;
    }

    public void setViewport(float width, float height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        debugBorderedRect.setSize(width, height);
    }

}
