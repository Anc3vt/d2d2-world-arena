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
