package com.ancevt.d2d2world.gameobject.action;

import com.ancevt.d2d2world.D2D2World;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.util.args.Args;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ancevt.d2d2world.script.JavaScriptEngine.calculate;

@Slf4j
public class ActionProgram {

    public static final ActionProgram STUB = new ActionProgram(List.of());

    private final @NotNull List<Action> actions;
    private int iterator;

    public ActionProgram(@NotNull List<Action> actions) {
        this.actions = actions;
    }

    public void process() {
        if (!D2D2World.isServer() || actions.isEmpty()) return;

        Action currentAction = actions.get(iterator);

        boolean goToNextAction = currentAction.process();

        if (goToNextAction) {
            iterator++;
            if (iterator >= actions.size()) {
                iterator = 0;
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
            Args values = new Args(split[2]);

            Runnable function = switch (word) {
                case "moveX" -> {
                    float val = calculate(values.get(String.class, 0));
                    yield () -> gameObject.moveX(val);
                }
                case "moveY" -> {
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
        iterator = 0;
        actions.forEach(Action::reset);
    }
}















