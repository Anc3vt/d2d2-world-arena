package ru.ancevt.d2d2world.gameobject.script;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.gameobject.IActioned;
import ru.ancevt.d2d2world.gameobject.IGameObject;

import java.util.Map;
import java.util.function.Consumer;

public class Action {
    private final IActioned gameObject;
    private final Map<String, Object> properties;
    private final Consumer<IGameObject> function;
    private final int count;
    private int step;

    public Action(@NotNull IActioned gameObject,
                  int count,
                  @NotNull Consumer<IGameObject> function,
                  Map<String, Object> properties) {

        this.gameObject = gameObject;
        this.count = count;
        this.properties = properties;
        this.function = function;
    }

    public Action(@NotNull IActioned gameObject,
                  int count,
                  @NotNull Consumer<IGameObject> function) {

        this(gameObject, count, function, null);
    }

    public @NotNull IActioned getGameObject() {
        return gameObject;
    }

    public boolean process() {
        step++;

        function.accept(gameObject);

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
