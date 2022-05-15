
package com.ancevt.d2d2world.gameobject;

import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.gameobject.action.ActionProgram;
import com.ancevt.d2d2world.gameobject.action.IScriptable;
import com.ancevt.d2d2world.mapkit.MapkitItem;

public class ScriptableActor extends Actor implements IActioned, IScriptable {

    private String script = "";
    private ActionProgram actionProgram;
    private String actionProgramData;

    public ScriptableActor(MapkitItem mapkitItem, int gameObjectId) {
        super(mapkitItem, gameObjectId);

        actionProgram = ActionProgram.STUB;
    }

    @Override
    public void setScript(@NotNull String script) {
        this.script = script;
    }

    @Override
    public @NotNull String getScript() {
        return script;
    }

    @Override
    public void setActionProgramData(@NotNull String actionProgramData) {
        this.actionProgramData = actionProgramData;
    }

    @Override
    public @NotNull String getActionProgramData() {
        return actionProgramData;
    }

    @NotNull
    @Override
    public ActionProgram getActionProgram() {
        return actionProgram;
    }
}
