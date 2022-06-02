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

package com.ancevt.d2d2world.gameobject.action;

import com.ancevt.d2d2world.gameobject.IGameObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Action {
    private final IGameObject gameObject;
    private final Map<String, Object> properties;
    private final Runnable function;
    private final int count;
    private int step;

    public Action(@NotNull IGameObject gameObject,
                  int count,
                  @NotNull Runnable function,
                  Map<String, Object> properties) {

        this.gameObject = gameObject;
        this.count = count;
        this.properties = properties;
        this.function = function;
    }

    public Action(@NotNull IGameObject gameObject,
                  int count,
                  @NotNull Runnable function) {

        this(gameObject, count, function, null);
    }

    public @NotNull IGameObject getGameObject() {
        return gameObject;
    }

    public boolean process() {
        step++;

        //if(gameObject.getName().equals("_test_platform_1")) {
        //    debug("Action:41: <b><A>process");
        //}
        function.run();

        if (step >= count) {
            step = 0;
            return true;
        }
        return false;
    }

    public void reset() {
        step = 0;
    }

    @Override
    public String toString() {
        return "Action{" +
                "gameObject=" + gameObject +
                ", properties=" + properties +
                ", function=" + function +
                ", count=" + count +
                ", step=" + step +
                '}';
    }


}
