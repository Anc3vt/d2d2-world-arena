package ru.ancevt.d2d2world.gameobject;

import org.jetbrains.annotations.NotNull;
import ru.ancevt.d2d2world.gameobject.script.ActionProgram;
import ru.ancevt.d2d2world.gameobject.script.IScriptable;
import ru.ancevt.d2d2world.gameobject.script.ScriptActionProgramConvector;
import ru.ancevt.d2d2world.mapkit.MapkitItem;

public class ScriptableActor extends Actor implements IActioned, IScriptable {

    private String script = "";
    private ActionProgram actionProgram;

    public ScriptableActor(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);

        actionProgram = ActionProgram.STUB;
    }

    @Override
    public void setScript(@NotNull String script) {
        this.script = script;
        setActionProgram(ScriptActionProgramConvector.convert(script));
    }

    @Override
    public @NotNull String getScript() {
        return script;
    }

    @Override
    public void setActionProgram(@NotNull ActionProgram actionProgram) {
        this.actionProgram = actionProgram;
    }

    @Override
    public @NotNull ActionProgram getActionProgram() {
        return actionProgram;
    }
}
