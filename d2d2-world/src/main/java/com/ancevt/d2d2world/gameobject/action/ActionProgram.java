package com.ancevt.d2d2world.gameobject.action;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.util.args.Args;

import java.util.ArrayList;
import java.util.List;

import static com.ancevt.d2d2world.script.JavaScriptEngine.calculate;

public class ActionProgram {

    public static final ActionProgram STUB = new ActionProgram(List.of());

    private final @NotNull List<Action> actions;
    private int iterator;

    public ActionProgram(@NotNull List<Action> actions) {
        this.actions = actions;
    }

    public void process() {
        if (actions.isEmpty()) return;

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
            String[] split = command.split(" ");

            int count = (int) calculate(split[0]);
            String word = split[1];
            Args values = new Args(split[2]);

            Runnable function = switch (word) {
                case "moveX" -> () -> gameObject.moveX(calculate(values.get(String.class, 0)));
                case "moveY" -> () -> gameObject.moveY(calculate(values.get(String.class, 0)));
                //case "moveXY" -> () -> gameObject.move(calculate(values,));
                default -> throw new IllegalStateException(command);
            };

            actions.add(new Action(gameObject, count, function));


            //() -> this.moveX(-1)));
        }
        return new ActionProgram(actions);
    }

    public void reset() {
        iterator = 0;
        actions.forEach(Action::reset);
    }
}















