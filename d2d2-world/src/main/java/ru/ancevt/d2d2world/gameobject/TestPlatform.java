package ru.ancevt.d2d2world.gameobject;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.gameobject.script.Action;
import ru.ancevt.d2d2world.gameobject.script.ActionProgram;
import ru.ancevt.d2d2world.mapkit.MapkitItem;

import java.util.ArrayList;
import java.util.List;

public class TestPlatform extends Platform {

    public TestPlatform(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);
        setActionProgram(createActionProgram());
    }

    private @NotNull ActionProgram createActionProgram() {
        List<Action> actions = new ArrayList<>();
        actions.add(new Action(this, 64, o -> o.moveX(-1)));
        actions.add(new Action(this, 64, o -> o.moveX(1)));
        return new ActionProgram(actions);
    }
}
