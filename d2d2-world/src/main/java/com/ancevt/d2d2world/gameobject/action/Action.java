
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
