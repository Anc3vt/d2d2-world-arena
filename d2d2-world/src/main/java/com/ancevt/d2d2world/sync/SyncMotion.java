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
package com.ancevt.d2d2world.sync;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Root;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2.event.InputEvent;
import com.ancevt.d2d2world.gameobject.PlayerActor;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.abs;

public class SyncMotion {

    private static final Map<IDisplayObject, MotionState> map = new ConcurrentHashMap<>();

    public static void moveMotion(@NotNull IDisplayObject o, float x, float y) {
        if(abs(o.getX() - x) > 8f && abs(o.getY() - y) > 8f) {
            o.setXY(x, y);
            return;
        }

        MotionState motionState = map.get(o);
        if (motionState == null) {
            motionState = new MotionState(x, y);
            map.put(o, motionState);
        }
        motionState.targetX = x;
        motionState.targetY = y;

        o.removeEventListener(SyncMotion.class, Event.REMOVE_FROM_STAGE);
        o.addEventListener(SyncMotion.class, Event.REMOVE_FROM_STAGE, event -> {
            map.remove(o);
            o.removeEventListener(SyncMotion.class, Event.REMOVE_FROM_STAGE);
        });
    }

    public static void clear() {
        map.clear();
    }

    public static void process() {
        List<IDisplayObject> toRemove = new LinkedList<>();

        map.forEach((displayObject, state) -> {
            float oX = displayObject.getX();
            float oY = displayObject.getY();

            float tX = state.targetX;
            float tY = state.targetY;

            float speedX = (tX - oX) / 3;
            float speedY = (tY - oY) / 2;

            displayObject.move(speedX, speedY);

            if ((abs(tX - oX) < 1f && abs(tY - oY) < 1f)) {
                displayObject.setXY(tX, tY);
            }

            if (displayObject instanceof PlayerActor playerActor && playerActor.isLocalPlayerActor()) {
                toRemove.add(displayObject);
            }
        });

        toRemove.forEach(map::remove);
    }

    @AllArgsConstructor
    private static class MotionState {
        private float targetX;
        private float targetY;
    }

    public static void main(String[] args) {
        Root root = D2D2.init(new LWJGLBackend(800, 600, "(floating"));

        Sprite sprite = new Sprite("satellite");

        root.add(sprite);

        root.addEventListener(InputEvent.MOUSE_DOWN, event -> {
            if (event instanceof InputEvent e) {
                SyncMotion.moveMotion(sprite, e.getX(), e.getY());
            }
        });

        root.addEventListener(Event.EACH_FRAME, event -> SyncMotion.process());

        D2D2.loop();
    }
}
