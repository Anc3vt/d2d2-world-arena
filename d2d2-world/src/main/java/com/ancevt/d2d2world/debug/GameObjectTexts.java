/*
 *   D2D2 World
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
package com.ancevt.d2d2world.debug;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2.display.DisplayObject;
import com.ancevt.d2d2.display.DisplayObjectContainer;
import com.ancevt.d2d2.display.text.BitmapText;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ancevt.d2d2.event.Event.REMOVE_FROM_STAGE;

public class GameObjectTexts extends DisplayObjectContainer {

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
                gameObject.addEventListener(GameObjectTexts.class, REMOVE_FROM_STAGE, this::gameObject_removeFromStage);
                bitmapTextMap.put(gameObject, bitmapText);
                add(bitmapText);
            }
            bitmapText.setXY(gameObject.getX(), gameObject.getY());
        });
    }

    private void gameObject_removeFromStage(@NotNull Event event) {
        if (event.getSource() instanceof IGameObject gameObject) {
            gameObject.removeEventListener(GameObjectTexts.class);
            BitmapText bitmapText = bitmapTextMap.get(gameObject);
            if (bitmapText != null) {
                Async.runLater(5, TimeUnit.SECONDS, bitmapText::removeFromParent);
                bitmapText.setColor(Color.DARK_GRAY);
            }
            bitmapTextMap.remove(gameObject);
        }
    }

}
