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
