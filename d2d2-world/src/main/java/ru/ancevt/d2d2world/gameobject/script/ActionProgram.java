package ru.ancevt.d2d2world.gameobject.script;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
            if(iterator >= actions.size()) {
                iterator = 0;
            }
        }
    }

    public static void main(String[] args) {

    }

    public void reset() {
        actions.forEach(Action::reset);
    }
}
