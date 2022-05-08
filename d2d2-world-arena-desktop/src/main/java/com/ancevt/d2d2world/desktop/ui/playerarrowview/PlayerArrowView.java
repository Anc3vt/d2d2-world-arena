/*
 *   D2D2 World Arena Desktop
 *   Copyright (C) 2022 Ancevt (me@ancevt.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2world.desktop.ui.playerarrowview;

import com.ancevt.d2d2.common.BorderedRect;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2world.gameobject.IGameObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerArrowView extends DisplayObjectContainer {

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
