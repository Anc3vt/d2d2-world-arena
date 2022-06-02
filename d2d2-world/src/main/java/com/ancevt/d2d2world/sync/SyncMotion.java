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

package com.ancevt.d2d2world.sync;

import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.backend.lwjgl.LWJGLBackend;
import com.ancevt.d2d2.display.IDisplayObject;
import com.ancevt.d2d2.display.Sprite;
import com.ancevt.d2d2.display.Stage;
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
        Stage stage = D2D2.init(new LWJGLBackend(800, 600, "(floating"));

        Sprite sprite = new Sprite("satellite");

        stage.add(sprite);

        stage.addEventListener(InputEvent.MOUSE_DOWN, event -> {
            if (event instanceof InputEvent e) {
                SyncMotion.moveMotion(sprite, e.getX(), e.getY());
            }
        });

        stage.addEventListener(Event.EACH_FRAME, event -> SyncMotion.process());

        D2D2.loop();
    }
}
