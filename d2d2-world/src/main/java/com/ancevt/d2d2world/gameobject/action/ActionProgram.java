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
import com.ancevt.d2d2world.gameobject.ISynchronized;
import com.ancevt.util.args.Args;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ancevt.commons.unix.UnixDisplay.debug;
import static com.ancevt.d2d2world.D2D2World.isServer;
import static com.ancevt.d2d2world.script.JavaScriptEngine.calculate;

@Slf4j
public class ActionProgram {

    public static final ActionProgram STUB = new ActionProgram(List.of());

    private final @NotNull List<Action> actions;
    private int currentIndex;
    private Action currentAction;

    public ActionProgram(@NotNull List<Action> actions) {
        this.actions = actions;
    }

    public int getCurrentActionIndex() {
        return currentIndex;
    }

    public void setCurrentActionIndex(int index) {
        currentIndex = index;
        currentAction = actions.get(currentIndex);
        currentAction.reset();
    }

    public void process() {
        if (!isServer() || actions.isEmpty()) return;

        currentAction = actions.get(currentIndex);

        boolean goToNextAction = currentAction.process();

        if (goToNextAction) {
            currentIndex++;
            if (currentIndex >= actions.size()) {
                currentIndex = 0;
            }
            if(isServer() && currentAction.getGameObject() instanceof ISynchronized iSynchronized) {
                iSynchronized.sync();
            }
        }
    }

    public static @NotNull ActionProgram parse(IGameObject gameObject, String actionProgramData) {
        List<Action> actions = new ArrayList<>();

        String[] commands = actionProgramData.split(";");
        for (String command : commands) {
            command = command.trim();

            String[] split = command.split(" ", 3);

            int count = (int) calculate(split[0]);
            String word = split[1];
            Args values = Args.of(split[2]);

            Runnable function = switch (word) {
                case "moveX" -> {
                    float val = calculate(values.get(String.class, 0));
                    yield () -> gameObject.moveX(val);
                }
                case "moveY" -> {

                    if(gameObject.getName().equals("_test_platform_1")) {
                        debug("ActionProgram:81: <A>moveY " + gameObject.getY());
                    }

                    float val = calculate(values.get(String.class, 0));
                    yield () -> gameObject.moveY(val);
                }
                case "moveXY" -> {
                    float toX = calculate(values.get(String.class, 0));
                    float toY = calculate(values.get(String.class, 1));
                    yield () -> gameObject.move(toX, toY);
                }
                default -> throw new IllegalStateException(command);
            };


            actions.add(new Action(gameObject, count, function));
        }
        return new ActionProgram(actions);
    }

    public void reset() {
        currentIndex = 0;
        actions.forEach(Action::reset);
    }
}















